(ns bitem.pias.client.core
  (:require
   [clojure.string :as cstr]
   [reagent.core :as reag]
   [reagent.dom :as rdom]
   ["halfmoon" :as moon]
   [bitem.pias.client.state :as state]
  #_ [bitem.pias.client.element.management.list :as mgmt-list]
  #_ [bitem.pias.client.element.management.edit :as mgmt-edit]))

(defn toggle-sidebar-btn []
  [:button {:class "btn btn-action"
            :type "button"
            :on-click (fn [] (moon/toggleSidebar))}
   [:i {:class "fa fa-bars"
        :aria-hidden "true"}]])

(defn brand-logo []
  [:a {:href "#"
       :class "navbar-text text-monospace"}
   [:p "bitem"]])
 
(defn change-color-btn []
  [:button {:class "btn btn-action"
            :on-click
            (fn []
              (swap! state/control assoc :dark-mode
                     (-> @state/control :dark-mode not))
              (moon/toggleDarkMode))}
   [:i {:class "fas fa-moon"
        :aria-hidden "true"}]])

(defn search-box []
  [:input {:type "text"
           :class "form-control"
           :placeholder "search"
           :required "required"}])

(defn sign-change-btn []
  [:button {:class "btn"
            :on-click #(swap! state/control assoc :signed false)}
   "Out"])

(defn version-label []
  [:span {:class "navbar-text text-monospace"}
   (str "v" "0.1")])

(defn section-link [k]
  [:li {:class (cstr/join " " ["nav-item"
                               (when (= (-> @state/control :element first) k)
                                 "active")])
        :on-click #(swap! state/control assoc :element [k])}
   [:a {:class "nav-link"} (-> k name cstr/capitalize)]])

(defn section-links []
  [:ul {:class "navbar-nav d-none d-md-flex"}
   (section-link :board)
   (section-link :management)
   (section-link :entity)
   (section-link :account)])

(defn nav-content []
  [:div {:class "navbar-content"}
   [toggle-sidebar-btn]
   [brand-logo]
   [version-label]
   [section-links]])

(defn nav-form []
  [:form {:class "form-inline d-none d-md-flex ml-auto"}
   (change-color-btn)
   (search-box)
   (sign-change-btn)])

(defn navbar []
  [:div {:class "page-wrapper with-navbar"}
   [:nav
    {:class "navbar"}
    (nav-content)
    (nav-form)]])

(defn side-item [icon subject]
  [:a {:class "sidebar-link sidebar-link-with-icon"}
   [:span {:class "sidebar-icon"}
    [:i {:class icon :aria-hidden true}]]
   subject])

(defn sidebar []
  [:div {:class "sidebar"}
   [:div {:class "sidebar-menu"}
    [:br] [:br]
    (case (-> @state/control :element first)
      :board [:<>
              (side-item "fa fa-terminal" "Entity")
              (side-item "fa fa-terminal" "Ownership")
              (side-item "fa fa-terminal" "Transaction")
              (side-item "fa fa-terminal" "Journal")
              (side-item "fa fa-terminal" "Ledger")
              (side-item "fa fa-terminal" "Report")]
      :management [:<> 
                   (side-item "fa fa-terminal" "List")
                   (side-item "fa fa-terminal" "Edit")
                   (side-item "fa fa-terminal" "Add")]
      :entity [:<> 
               (side-item "fa fa-terminal" "Entity")
               (side-item "fa fa-terminal" "Ownership")
               (side-item "fa fa-terminal" "Transaction")
               (side-item "fa fa-terminal" "Journal")
               (side-item "fa fa-terminal" "Ledger")
               (side-item "fa fa-terminal" "Report")]
      :account [:<> 
                (side-item "fa fa-terminal" "Entity")
                (side-item "fa fa-terminal" "Ownership")
                (side-item "fa fa-terminal" "Transaction")
                (side-item "fa fa-terminal" "Journal")
                (side-item "fa fa-terminal" "Ledger")
                (side-item "fa fa-terminal" "Report")])]])

(defn app-pg []
  [:div
   [navbar]
   [sidebar]])

(defn sign-on-btn []
  [:button {:class "btn"
            :on-click #(swap! state/control assoc :signed true)}
   "Sign in!"])

(defn sign-pg []
  [sign-on-btn])

(defn scaffold
  "the selector of all pages."
  []
  (if (:signed @state/control)
    [app-pg]
    [sign-pg]))

(defn run []
  (swap! state/control assoc :dark-mode true)
  (print @state/control)
  (rdom/render
   [scaffold]
   (js/document.getElementById "root")))

(defn ^:export init []
  (moon/toggleDarkMode)
  (run)
  (print "initialized and loaded"))

(defn ^:export refresh []
  (run)
  (swap! state/control assoc :element [:management :add])
  (print "hot-reloaded"))

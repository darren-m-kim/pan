(ns bitem.pias.client.core
  (:require
   [clojure.string :as cstr]
   [reagent.core :as reag]
   [reagent.dom :as rdom]
   ["halfmoon" :as moon]
   [bitem.pias.client.state :as state]
   [bitem.pias.client.util :as util]
   [bitem.pias.client.element.sign.in :as sign-in]
   [bitem.pias.client.element.management.list :as mgmt-list]
   [bitem.pias.client.element.management.edit :as mgmt-edit]))

(defn toggle-sidebar-btn []
  [:button {:class "btn btn-action"
            :type "button"}
   [:i {:class "fa fa-bars"
        :aria-hidden "true"}]])

(defn brand-logo []
  [:a {:href "#"
       :class "navbar-text text-monospace"}
   [:p "bitem"]])
 
(defn change-color-btn []
  (let [current-mode (-> @state/control :dark-mode)
        flipping (fn []
                   (swap! state/control assoc :dark-mode (not current-mode))
                   (moon/toggleDarkMode)
                   (print @state/control))]
    [:button {:class "btn btn-action"
              :on-click flipping}
     [:i {:class "fas fa-moon"
          :aria-hidden "true"}]]))

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
        :on-click #(swap! state/control assoc :element [k nil])}
   [:a {:class "nav-link"} (-> k name cstr/capitalize)]])

(defn section-links []
  [:ul {:class "navbar-nav d-none d-md-flex"}
   (section-link :board)
   (section-link :management)
   (section-link :entity)
   (section-link :account)])

(defn status []
  (let [mgmt-name (or (-> @state/control :management)
                      "Not Selected")
        person-name (-> @state/control :person :name)]
    [:span {:class "navbar-text"}
     (str "[ " mgmt-name " - " person-name " ]")]))

(defn nav-content []
  [:div {:class "navbar-content"}
   [toggle-sidebar-btn]
   [brand-logo]
   [version-label]
   [section-links]
   [status]])

(defn nav-form []
  [:form {:class "form-inline d-none d-md-flex ml-auto"}
   [change-color-btn]
   [search-box]
   [sign-change-btn]])

(defn navbar []
  [:nav
   {:class "navbar"}
   [nav-content]
   [nav-form]])

(defn side-item [icon subject & add-fn]
  (let [current-elem (-> @state/control :element)
        new-elem [(first current-elem) subject]]
    [:a (merge (util/class :sidebar-link
                           :sidebar-link-with-icon
                           :nav-item nil
                           (when (= (second current-elem) subject) :text-success))
               {:on-click (fn []
                            (when add-fn ((first add-fn)))
                            (swap! state/control assoc :element new-elem))})
     [:span {:class "sidebar-icon"}
      [:i {:class icon :aria-hidden true}]]
     (-> subject name cstr/capitalize)]))

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
      :management
      [:<> 
       (side-item "fa fa-terminal" :list)
       (side-item "fa fa-terminal" :edit #(reset! mgmt-edit/entity (-> @state/control :management)))
       (side-item "fa fa-terminal" :add)]
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

(defn element-section []
  [:div {:class "content-wrapper"}
   (case (-> @state/control :element)
     [:management :list] [mgmt-list/table]
     [:management :edit] [mgmt-edit/unit]  

     )])

(defn app-pg []
  [:div {:class "page-wrapper with-navbar with-sidebar with-navbar-fixed-bottom"}
   [navbar]
   [sidebar]
   [element-section]])

(defn scaffold
  "the selector of all pages."
  []
  (if (:signed @state/control)
    [app-pg]
    [sign-in/page]))

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
  (print "hot-reloaded"))

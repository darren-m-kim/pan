(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            ["halfmoon" :as moon]
            [app.parser :as p]))

(defonce dark (r/atom true))
(defonce client (r/atom nil))
(defonce worker (r/atom nil))

(defn show-db []
  (merge {:dark @dark}
         {:client @client}
         {:worker @worker}))

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
              (do (swap! dark (fn [mode] (not mode)))
                  (moon/toggleDarkMode)))}
   [:i {:class "fas fa-moon"
        :aria-hidden "true"}]])

(defn search-box []
  [:input {:type "text"
           :class "form-control"
           :placeholder "search"
           :required "required"}])

(defn sign-change-btn []
  [:button {:class "btn"
            :on-click (fn [] (reset! worker nil))}
   "Sign Out"])

(defn version-label []
  [:span {:class "navbar-text text-monospace"}
   (p/combine "v" "0.1")])

(defn service-labels []
  [:ul {:class "navbar-nav d-none d-md-flex"} " "
   [:li {:class "nav-item"}
    [:a {:href "#", :class "nav-link"}
     "Docs"]]
   [:li {:class "nav-item"}
    [:a {:href "#", :class "nav-link"}
     "Products"]]])

(defn nav-content []
  [:div {:class "navbar-content"}
   (toggle-sidebar-btn)
   (brand-logo)
   (version-label)
   (service-labels)])

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

(defn item-in-side-bar [icon subject]
  [:a {:href "#"
       :class "sidebar-link sidebar-link-with-icon"}
   [:span {:class "sidebar-icon"}
    [:i {:class icon
         :aria-hidden true}]]
   subject])

(defn sidebar []
  [:div {:class "sidebar"}
   [:div {:class "sidebar-menu"}
    [:br]
    [:h5 {:class "sidebar-title"}
     "Components"]
    [:div {:class "sidebar-divider"}]    
    (item-in-side-bar "fa fa-terminal" "Entity")
    (item-in-side-bar "fa fa-terminal" "Ownership")
    (item-in-side-bar "fa fa-terminal" "Transaction")
    (item-in-side-bar "fa fa-terminal" "Journal")
    (item-in-side-bar "fa fa-terminal" "Ledger")
    (item-in-side-bar "fa fa-terminal" "Report")]])

(defn dash-pg []
  [:div
   [navbar]
   [sidebar]])

(defn sign-on-btn []
  [:button
   {:on-click (fn []
                (reset! client 123)
                (reset! worker 456))}
   "abc"])

(defn sign-pg []
  [sign-on-btn])

(defn scaffold
  "the selector of all pages."
  []
  (println (show-db))
  (if (some nil? [@client @worker])
    [sign-pg]
    [dash-pg]))

(defn run []
  (rdom/render
   [scaffold]
   (js/document.getElementById "root")))

(defn ^:export init []
  (reset! dark true)
  (moon/toggleDarkMode)
  (run)
  (js/console.log "Loaded"))

(defn ^:export refresh []
  (run)
  (js/console.log "Hot reload"))

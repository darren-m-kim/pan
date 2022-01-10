(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            ["halfmoon" :as moon]
            [app.parser :as p]))

(defonce state-db (r/atom {}))

(defn toggle-sidebar-btn []
  [:button {:class "btn btn-action"
            :type "button"
            :on-click (fn [] (moon/toggleSidebar))}
   [:i {:class "fa fa-bars"
        :aria-hidden "true"}]])

(defn brand-logo []
  [:a {:href "#"
       :class "navbar-text text-monospace"}
   [:img {:src "...", :alt "atomic"}]])

(defn change-color-btn []
  [:button {:class "btn btn-action"
            :on-click
            (fn []
              (do (swap! state-db
                         #(assoc % :dark-mode
                                 (-> @state-db :dark-mode not)))
                  (moon/toggleDarkMode)))}
   [:i {:class "fas fa-moon"
        :aria-hidden "true"}]])

(defn search-box []
  [:input {:type "text"
           :class "form-control"
           :placeholder "search"
           :required "required"}])

(defn sign-change-btn []
  [:button {:class "btn btn-primary"
            :type "submit"}
   "Sign up"])

(defn version-label []
  [:span {:class "navbar-text text-monospace"}
   (p/combine "v0.1" "**")])

(defn service-labels []
  [:ul {:class "navbar-nav d-none d-md-flex"} " "
   [:li {:class "nav-item active"}
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
    (item-in-side-bar "fa fa-terminal" "Dashboard?")
    (item-in-side-bar "fa fa-terminal" "Journal")
    (item-in-side-bar "fa fa-camera-retro fa-lg" "Balance")]])

(defn scaffold []
  [:div
   (navbar)
   (sidebar)])

(defonce id-to-html "root")

(defn run []
  (rdom/render
   (scaffold)
   (js/document.getElementById id-to-html)))

(defn ^:export init []
  (swap! state-db (fn [db] (assoc db :dark-mode true)))
  (moon/toggleDarkMode)
  (run)
  (js/console.log "Loaded"))

(defn ^:export refresh []
  (run)
  (js/console.log "Hot reload"))

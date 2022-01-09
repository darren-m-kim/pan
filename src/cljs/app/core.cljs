(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defn navbar []
  [:div {:class "page-wrapper with-navbar"}
   [:nav {:class "navbar"}
    [:div {:class "navbar-content"}
     [:button {:class "btn btn-action", :type "button"}
      [:i {:class "fa fa-bars", :aria-hidden "true"}]
      [:span {:class "sr-only"} "Toggle sidebar"]" "]]  
    [:a {:href "#", :class "navbar-brand"}
     [:img {:src "...", :alt "..."}]]  
    [:span {:class "navbar-text text-monospace"} "v0.1"] " "  
    [:ul {:class "navbar-nav d-none d-md-flex"} " "
     [:li {:class "nav-item active"}
      [:a {:href "#", :class "nav-link"} "Docs"]]
     [:li {:class "nav-item"}
      [:a {:href "#", :class "nav-link"} "Products"]]]
    [:form {:class "form-inline d-none d-md-flex ml-auto"
            :action "..."
            :method "..."}  
     [:input {:type "text"
              :class "form-control"
              :placeholder "Email address"
              :required "required"}]
     [:button {:class "btn btn-primary"
               :type "submit"}
      "Sign up"]]  
    [:div {:class "navbar-content d-md-none ml-auto"} " "  
     [:div {:class "dropdown with-arrow"}
      [:button {:class "btn"
                :data-toggle "dropdown"
                :type "button"
                :id "navbar-dropdown-toggle-btn-1"}
       [:i {:class "fa fa-angle-down"
            :aria-hidden "true"}]]
      [:div {:class "dropdown-menu dropdown-menu-right w-200"
             :aria-labelledby "navbar-dropdown-toggle-btn-1"} " "  
       [:a {:href "#", :class "dropdown-item"} "Docs"]
       [:a {:href "#", :class "dropdown-item"} "Products"]
       [:div {:class "dropdown-divider"}]
       [:div {:class "dropdown-content"}
        [:form {:action "...", :method "..."}
         [:div {:class "form-group"}
          [:input {:type "text"
                   :class "form-control"
                   :placeholder "Email address"
                   :required "required"}]]
         [:button {:class "btn btn-primary btn-block"
                   :type "submit"}
          "Sign up"]]]]]]]  
   [:div {:class "content-wrapper"}
    "Futix"]])

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
    (item-in-side-bar "fa fa-terminal" "Dashboard")
    (item-in-side-bar "fa fa-terminal" "Journal")
    (item-in-side-bar "fa fa-camera-retro fa-lg" "Balance")]])

#_(defn change-color-mode []
  [:div
   [:input {:type "button" :value "Click me!"
            :on-click (fn [] halfmoon/toggleDarkMode)}]])

(defn scaffold []
  [:div
   [navbar]
   [sidebar]
  ;; [change-color-mode]
   [:h2 "abcdef"]])

(def id-to-html "root")

(defn run []
  (rdom/render [scaffold]
               (js/document.getElementById id-to-html)))

(defn ^:export init []
  (run)
  (js/console.log "Loaded"))

(defn ^:export refresh []
  (run)
  (js/console.log "Hot reload"))

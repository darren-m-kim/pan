(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            ["halfmoon" :as moon]
            [app.parser :as p]))

(defonce dark (r/atom true))
(defonce client (r/atom nil))
(defonce worker (r/atom nil))
(defonce page (r/atom nil))

(defn show-db []
  (merge {:dark @dark}
         {:client @client}
         {:worker @worker}))

(defn sign-in-crd []
  [:div {:class "w-400 mw-full"}
   [:div {:class "card p-1"}
    [:div {:class "px-card py-30"}
     [:h3 {:class "text-center"}
      "Bitem"]
     [:h5 {:class "text-center"}
      "Investment Accounting Suite"]]
    [:div {:class "content"}
     [:form
      [:div {:class "form-group input-group"}
       [:div {:class "input-group-prepend"}
        [:span {:class "input-group-text"}
         [:i {:class "fa fa-envelope-o",
              :aria-hidden "true"}]]]
       [:input {:type "email",
                :class "form-control",
                :placeholder "email",
                :required "required"}]]
      [:div {:class "form-group input-group"}
       [:div {:class "input-group-prepend"}
        [:span {:class "input-group-text"}
         [:i {:class "fa fa-envelope-o",
              :aria-hidden "true"}]]]
       [:input {:type "password",
                :class "form-control",
                :placeholder "password",
                :required "required"}]]
      [:input {:class "btn btn-primary btn-block",
               :value "Sign In"
               :on-click (fn []
                           (reset! client 123)
                           (reset! worker 456)
                           (reset! page :dash))}]]]
    [:div {:class "px-card py-10 bg-light-lm bg-very-dark-dm rounded-bottom"}
     [:p {:class "font-size-12 m-0"}
      "We will notify you whenever we make a new post. No spam, no marketing, we promise."]]]])

(defn toggle-sidebar-btn []
  [:button {:class "btn btn-action"
            :type "button"}
   [:i {:class "fa fa-bars"
        :aria-hidden "true"}]])

(defn brand-logo []
  [:a {:href "#"
       :class "navbar-text text-monospace"}
   [:p "Bitem"]])
 
(defn change-color-btn []
  [:button {:class "btn btn-action"
            :on-click
            (fn []
              (do (swap! dark #(not %))
                  (moon/toggleDarkMode)))}
   [:i {:class "fas fa-moon"
        :aria-hidden "true"}]])

(defn search-box []
  [:input {:type "text"
           :class "form-control"
           :placeholder "search"
           :required "required"}])

(defn sign-out-btn []
  [:button {:class "btn"
            :on-click (fn []
                        (reset! worker nil)
                        (reset! client nil)
                        (reset! page :sign-in))}
   "Sign Out"])

(defn version-lb []
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
   [toggle-sidebar-btn]
   [brand-logo]
   [version-lb]
   [service-labels]])

(defn nav-form []
  [:form {:class "form-inline d-none d-md-flex ml-auto"}
   [change-color-btn]
   [search-box]
   [sign-out-btn]])

(defn nav-bar []
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

(defn side-bar []
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
   [nav-bar]
   [side-bar]])

(defn sign-in-pg []
  [:div {:class "page-wrapper"}
   [:div {:class "content-wrapper"}
    [sign-in-crd]]])

(defn pg-selector
  "selects page per values in client and worker."
  []
  (println (show-db))
  (case @page
    :intro nil
    :sign-in [sign-in-pg]
    :dash [dash-pg]
    [sign-in-pg]))

(defn run []
  (rdom/render
   [pg-selector]
   (js/document.getElementById "root")))

(defn ^:export init []
  (reset! dark true)
  (moon/toggleDarkMode)
  (run)
  (js/console.log "Loaded"))

(defn ^:export refresh []
  (run)
  (js/console.log "Hot reload"))

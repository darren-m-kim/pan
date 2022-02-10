(ns client.state
  (:require [reagent.core :as r]))


(defonce dark (r/atom true))
(defonce client (r/atom {:legalname "Bitem, LLC"
                         :description "Software Firm"}))
(defonce worker (r/atom nil))
(defonce page (r/atom nil))
(defonce account (r/atom nil))

(defn show-db []
  (merge {:dark @dark}
         {:page @page}
         {:client @client}
         {:worker @worker}))



(comment


(def sample-chart-of-accounts
  [{:number 101 :title "Cash" :increased-by :debit :desc "Cash and equivalents"}
   {:number 101 :title "Cash" :increased-by :debit :desc "Cash and equivalents"}
   {:number 101 :title "Cash" :increased-by :debit :desc "Cash and equivalents"}
   {:number 101 :title "Cash" :increased-by :debit :desc "Cash and equivalents"}
   {:number 101 :title "Cash" :increased-by :debit :desc "Cash and equivalents"}
   {:number 101 :title "Cash" :increased-by :debit :desc "Cash and equivalents"}
   {:number 101 :title "Cash" :increased-by :debit :desc "Cash and equivalents"}
   {:number 101 :title "Cash" :increased-by :debit :desc "Cash and equivalents"}
   {:number 101 :title "Cash" :increased-by :debit :desc "Cash and equivalents"}
   {:number 101 :title "Cash" :increased-by :debit :desc "Cash and equivalents"}])

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
                           (reset! state/client 123)
                           (reset! state/worker 456)
                           (reset! state/page :dash))}]]]
    [:div {:class "px-card py-10 bg-light-lm bg-very-dark-dm rounded-bottom"}
     [:p {:class "font-size-12 m-0"}
      "We will notify you whenever we make a new post. No spam, no marketing, we promise."]]]])

(defn brand-logo []
  [:a {:href "#"
       :class "navbar-text text-monospace"}
   [:p "Bitem"]])
 
(defn change-color-btn []
  [:button {:class "btn btn-action"
            :on-click
            (fn []
              (do (swap! state/dark #(not %))
                  (moon/toggleDarkMode)
                  (go (let [response (<! (http/get
                                          "http://localhost:3548/test"
                                          {:with-credentials? false}))]
                        (prn (:body response))))))}
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
                        (reset! state/worker nil)
                        (reset! state/client nil)
                        (reset! state/page :sign-in))}
   "Sign Out"])

(defn version-lb []
  [:span {:class "navbar-text text-monospace"}
   (p/combine "v" "1.0")])

(defn menu-button [p]
   [:li {:class "nav-item"}
    [:a {:class "nav-link"
         :on-click (fn [] (reset! state/page p))}
     (clojure.string/capitalize (name p))]])

(defn service-labels []
  [:ul {:class "navbar-nav d-none d-md-flex"} " "
   (menu-button :entity)
   (menu-button :transaction)
   (menu-button :account)
   (menu-button :template)
   (menu-button :journal)
   (menu-button :trend)
   (menu-button :client)
   (menu-button :enduser)])

(defn nav-content []
  [:div {:class "navbar-content"}
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
   [:p "1234"]])

(defn client-page []
  [:div {:class "page-wrapper with-navbar"}
   [nav-bar]
   [:div {:class "content-wrapper"}
    [:div {:class "content"}
     [:h2 {:class "content-title"}
      (:legalname @state/client)]
     [:p (:description @state/client)]]]])

(defn account-page []
  [:div {:class "page-wrapper with-navbar"}
   [nav-bar]
   [:div {:class "content-wrapper"}
    [:div {:class "content"}
     [:h2 {:class "content-title"}
      "chart of accounts"]
     (map (fn [a] [:p (:title a)]) @state/account)]]])

(defn sign-in-pg []
  [:div {:class "page-wrapper"}
   [:div {:class "content-wrapper"}
    [sign-in-crd]]])

(defn pg-selector
  "selects page per values in client and worker."
  []
  (println (state/show-db))
  (case @state/page
    :entity [dash-pg]
    :transaction [dash-pg]
    :account [account-page]
    :template [dash-pg]
    :journal [dash-pg]
    :trend [dash-pg]
    :client [client-page]
    :enduser [dash-pg]
    :sign-in [sign-in-pg]
    :dash [dash-pg]
    [sign-in-pg]))

  )

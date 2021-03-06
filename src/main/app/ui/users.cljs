(ns app.ui.users
  (:require
                                        ;[com.fulcrologic.semantic-ui.elements.input :as ui-input]
   [app.model.user :as user]
   [com.fulcrologic.semantic-ui.elements.input.ui-input :refer [ui-input]]
   [com.fulcrologic.semantic-ui.elements.icon.ui-icon :refer [ui-icon]]
   [com.fulcrologic.semantic-ui.elements.loader.ui-loader :refer [ui-loader]]
   [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
                                        ;["react-country-flags" :as Flag]
   [app.application :as a :refer [SPA]]
                                        ;["react-flags" :as Flag]
   [app.model.session :as session]
   ["react-table" :as react-table]
   ["react-world-flags" :as Flag]
   ["react-flag-kit" :as FlagIcon ]
   ["react-sticky-table" :as StickyTable]
   [app.model.resource :as resource]
   [com.fulcrologic.fulcro.networking.http-remote :as net]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [app.model.work-line :as work-line]
   ["semantic-ui-react/dist/commonjs/modules/Dropdown/Dropdown" :default Dropdown]
   [clojure.set :as set]
   [clojure.string :as str]
   [app.math :as math]
   ["react-number-format" :as NumberFormat]
   ["react-country-flag" :as  ReactCountryFlag]
                                        ;["react-collapsing-table" :as ReactCollapsingTable]

                                        ;["react-calendar-timeline" :as TimeLine]
   ["semantic-ui-calendar-react" :as SemanticUICalendar]
   ["react-timeline-9000" :as ReactTimeLine]
   [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h3 button tr td table thead th tbody tfoot]]
   [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
   [tick.alpha.api :as t]
   [com.fulcrologic.fulcro.dom.html-entities :as ent]
   [com.fulcrologic.fulcro.dom.events :as evt]
   [com.fulcrologic.fulcro.algorithms.denormalize :as denormalize]
   [com.fulcrologic.semantic-ui.elements.image.ui-image :refer [ui-image]]
   [com.fulcrologic.semantic-ui.elements.flag.ui-flag :refer [ui-flag]]
   [app.model.item :as item]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.ui-state-machines :as uism :refer [defstatemachine]]
   [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
   [com.fulcrologic.fulcro.algorithms.merge :as merge]
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]

   [com.fulcrologic.semantic-ui.collections.form.ui-form-checkbox :refer [ui-form-checkbox]]



   ;; semantic comoponents 
   [com.fulcrologic.semantic-ui.modules.accordion.ui-accordion :refer [ui-accordion]]
   [com.fulcrologic.semantic-ui.modules.accordion.ui-accordion-title :refer [ui-accordion-title]]
   [com.fulcrologic.semantic-ui.modules.accordion.ui-accordion-content :refer [ui-accordion-content]]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu-item :refer [ui-menu-item]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-checkbox :refer [ui-form-checkbox]]
   ["semantic-ui-react/dist/commonjs/collections/Menu/Menu" :default Menu]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-group :refer [ui-form-group]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-field :refer [ui-form-field]]

   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.modules.checkbox.ui-checkbox :refer [ui-checkbox]]
   [com.fulcrologic.semantic-ui.elements.divider.ui-divider :refer [ui-divider]]

   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]]
   [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]]

   [com.fulcrologic.semantic-ui.elements.list.ui-list-content :refer [ui-list-content]]
   ["semantic-ui-react/dist/commonjs/modules/Checkbox/Checkbox" :default Checkbox]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu-menu :refer [ui-menu-menu]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list :refer [ui-list]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]

   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]

   

   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   [cljs-time.core :as tt]
   [cljs-time.format :as tf]
   [cljs-time.coerce :as tc]
   [taoensso.timbre :as log]


                                        ;[com.fulcrologic.semantic-ui.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]





   [com.fulcrologic.semantic-ui.collections.table.ui-table-row :refer [ui-table-row]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table :refer [ui-table]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header-cell :refer [ui-table-header-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header :refer [ui-table-header]]

   [com.fulcrologic.semantic-ui.collections.table.ui-table-body :refer [ui-table-body]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-cell :refer [ui-table-cell]]
                                        ;[com.fulcrologic.semantic-ui.button.ui-button :refer [ui-button]]

   ))









(defsc Resource
  [this {:resource/keys [id name email-address fluxod-name profile active? allow-actuals? allow-forecast? hlcr] :as props}]
  {:query [:resource/id :resource/name  :resource/email-address :resource/fluxod-name :resource/active? :resource/profile
           :resource/hlcr
           :resource/allow-actuals? :resource/allow-forecast?
           [:component/id :session]]
   :ident :resource/id
   #_#_:pre-merge (fn [{:keys [current-normalized data-tree]}]
                
                (merge data-tree
                       {:resource/profile (or (merge/nilify-not-found (:resource/profile data-tree))
                                              :profile/user)}))
   }
  
  
  (let [current-profile (-> props (get [:component/id :session]) :resource/profile)]
    (js/console.log "Profile: " current-profile)
    (ui-table-row
    {}
    [(ui-table-cell
      {:textAlign :right}
      name)
     
     (ui-table-cell
      {}
      (ui-form
       {}
       (ui-form-field
        {}
        (ui-input
         {:size :mini
          :value (if (string? fluxod-name) fluxod-name "")
          :onChange (fn [e e2] (m/set-string! this :resource/fluxod-name :event e))
          :onBlur (fn [e] (comp/transact! this [(user/save-fluxod-name {:resource/fluxod-name fluxod-name
                                                                        :resource/id id})]))}
         ))))
     (ui-table-cell
      {:textAlign :right}
      email-address)


     (cond
       (= current-profile :profile/team-leader)
       
       (ui-table-cell {:textAlign :right}
                      ({:profile/admin "Admin"
                          :profile/project-manager "Project Manager"
                          :profile/user "User"
                          :profile/project-leader "Project Leader"} profile))

       (= current-profile :profile/admin)
       (ui-table-cell
        {:singleLine true :textAlign :right}
        (ui-dropdown
         {:compact true :basic true 
          :options [{:text "Admin" :value :profile/admin}
                    {:text "Team Leader" :value :profile/team-leader}
                    {:text "Project Manager" :value :profile/project-manager}
                    {:text "User" :value :profile/user}]

          ;; TODO handle this on the server 
          :value  profile
          :onChange (fn [event data]
                      (comp/transact!
                       this
                       [(resource/set-resource-profile {:value (keyword (str "profile/" (.-value data))) :id id})]))
          }))
       :else
       (ui-table-cell {:textAlign :right} ""))
     (ui-table-cell
      {:textAlign :right}
      (ui-form
       {}
       (ui-form-field
        {}
        (ui-form-checkbox
         {
          :checked active?
          :onChange
          (fn [e d]
            (m/toggle! this :resource/active?)
            (comp/transact! this [(resource/set-resource-active?
                                   {:value  (.-checked d) :id id})]))}))))

     (ui-table-cell
      {:textAlign :right}
      (ui-form
       {}
       (ui-form-field
        {}
        (ui-form-checkbox
         {
          :checked allow-actuals?
          :onChange
          (fn [e d]
            #_(m/toggle! this :resource/active?)
            (comp/transact! this [(resource/set-resource-actuals?
                                   {:value  (.-checked d)})]))}))))

     (ui-table-cell
      {:textAlign :right}
      (ui-form
       {}
       (ui-form-field
        {}
        (ui-form-checkbox
         {
          :checked allow-forecast?
          :onChange
          (fn [e d]
            
            (comp/transact! this [(resource/set-resource-forecast?
                                   {:value  (.-checked d) :id id})]))}))))

     (cond
       (= current-profile :profile/team-leader)
       
       (ui-table-cell {:singleLine true :textAlign :right}
                      (or
                       ({:currency/CAD "CAD"
                         :currency/EUR "EUR"
                         :currency/USD "USD"} hlcr) ""))

       (= current-profile :profile/admin)
       (ui-table-cell
        {:textAlign :right :singleLine true}
        (ui-dropdown
         {:compact true :basic true 
          :options [{:text "EUR" :value :currency/EUR}
                    {:text "CAD" :value :currency/CAD}
                    {:text "USD" :value :currency/USD}]

          ;; TODO handle this on the server 
          :value  (or hlcr "Not found")
          :onChange (fn [event data]
                      (comp/transact!
                       this
                       [(resource/set-resource-hlcr {:value (keyword (str "currency/" (.-value data))) :id id})]))
          }))
       :else
       (ui-table-cell {:textAlign :right} ""))]))

  )



(def ui-resource (comp/factory Resource {:keyfn :resource/id}))

#_(defmutation set-resources
    [{:keys [id]}]
    (action [{:keys [state]}]
            ))


(defsc AdminUsers [this {:admin-users/keys [resources] :as props}]
  {:query         [{:admin-users/resources (comp/get-query Resource)} [::uism/asm-id ::session/session]]
   :ident         (fn [] [:component/id :admin-users])
   :route-segment ["admin-users"]
                                        ;:initLocalState (fn [this _] {:project nil :resource (:workplan/resource (comp/props this))})
   #_#_:componentDidMount (fn [this] (comp/transact! (set-resources) ))
   :will-enter (fn [app route-params]
                 (dr/route-deferred
                  [:component/id :admin-users]
                  (fn []
                    (df/load! app :resource/all-resources Resource {:post-mutation `dr/target-ready
                                                                    :post-mutation-params  {:target [:component/id :admin-users]}
                                                                    :target
                                                                    [:component/id :admin-users :admin-users/resources]})
                    
                    )))
   :initial-state (fn [params] {:admin-users/resources []})

   
   }

  (let [current-state (uism/get-active-state this ::session/session)
        logged-in? (= :state/logged-in current-state)]
    (if logged-in?
      (ui-container {:style {:width "80%"}}
                    
                    (dom/h3 {:style {:textAlign "center"}} "Users" )

                    (ui-table
                     {:style
                      {:fontSize   "85%"
                       :compact    true
                       :selectable true}
                      :celled true
                      :striped true
                      :color :blue}
                     (ui-table-header
                      {:fullWidth true :style {:position "sticky" :top 0}}
                      (ui-table-row
                       {}
                       (mapv  #(ui-table-header-cell {:textAlign :right
                                                      :style {:position "sticky" :top 0
                                                              :zIndex 2}} %) ["MS Name" "Fluxod Name" "Email" "Profile"
                                                                              "Enable" "Allow Actuals"
                                                                              "Allow Forecast"
                                                                              "HLCR"])))
                     (ui-table-body
                      {}
                      (mapv ui-resource resources))))
      (ui-segment {:style {:textAlign "center"}}
                  (div :.ui.container  "Please login with Fluxym account")))))








(ns app.model.project
  (:require [com.fulcrologic.fulcro.mutations :as mutations :refer [defmutation]]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.algorithms.normalized-state :as ns]
            [com.fulcrologic.fulcro.algorithms.denormalize :as denormalize]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [app.application :as a :refer [SPA]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [clojure.set :as set]
            [app.math :as math]
            
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            [taoensso.timbre :as log]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))



;; Actions 

(defn action-valid?
  [{:action/keys [action owner] :as form} field]
  (try
    (case field
      :action/owner (boolean (seq owner))
      :action/action (boolean (seq action))
      true)
    (catch :default _
      false)))


(def action-validator (fs/make-validator action-valid?))




(defmutation save-action
  "Unchecked mutation. Sends the given diff to the server without checking validity. See try-save-item."
  [{:db/keys [id]
    :keys      [diff]
    :as        params}]
  (action [{:keys [app state]}]
          (swap! state assoc-in [:action/id id :ui/saving?] true)
          (let [ProjectPanelQ (comp/registry-key->class :app.ui.projects/ProjectPanelQ)]
            ))
  (remote [env] (let [ActionListLabel (comp/registry-key->class :app.ui.projects/ActionListLabel)]
                    #_(-> env
                        (m/returning ActionListLabel)
                        (m/with-target
                          [:component/id :project-panel :>/action-list-label]))
                    true))
  
  (ok-action [{:keys [state tempid->realid] :as env}]
             (let [id (or (tempid->realid id) id)]
               (swap! state (fn [s]
                              (-> s
                                  (fs/entity->pristine* [:action/id id])
                                  (update-in [:action/id id] assoc :ui/new? false :ui/saving? false :ui/modal-open? false))))
               ))
  (error-action [{:keys [state]}]
                (js/alert "Failed to save item")
                (swap! state (fn [s]
                               (-> s
                                   (update-in [:action/id id] assoc :ui/saving? false))))))


(defmutation remove-action [{:db/keys [id] :keys [action-list]}]
  (action [{:keys [state]}]
          (swap! state (fn [s]
                         (-> s
                             (merge/remove-ident* [:action/id id] [:action-list/id action-list :action-list/actions])
                             (update :action/id dissoc id)))))
  (remote [env] true))




(defmutation remove-fluxod-project-name [{:db/keys [id] :keys [name id]}]
  (action [{:keys [state ref] :as env}]
          (let [current-names (ns/get-in-graph @state (conj ref :project-info/fluxod-project-names))]

            (js/console.log "NAMEES" current-names)
            (ns/update-caller! env assoc :project-info/fluxod-project-names (vec (remove #(= % name) current-names)) )))
  (remote [env] true))


(defmutation save-fluxod-client-name [{:keys [name id]}]
  (remote [env] true))






(defmutation set-info-as-active-menu [params]
  (action [{:keys [state] :as env}]
          (swap! state (fn [s]
                         (-> s
                             (update-in [:component/id :project-panel :ui/active-item] (constantly :info)))))))






(defmutation try-save-action [{:db/keys [id]
                             :keys      [diff]
                             :as        params}]

  
  (action [{:keys [app state]}]
          (let [state-map       @state
                ident           [:action/id id]
                completed-state (fs/mark-complete* state-map ident)
                action            (get-in completed-state ident)
                ActionRow (comp/registry-key->class :app.ui.projects/ActionRow)
                action-props      (fdn/db->tree (comp/get-query ActionRow)  action completed-state)
                valid?          (= :valid (action-validator (log/spy :info action-props)))]

            (js/console.log " ACTION -" (action-validator  action-props))
            (js/console.log "id " id)
            (if valid?
              (comp/transact! app [(save-action params)])
              (reset! state completed-state)))))

;; TODO organize 

;; GOV REVIEW WEEK


(defmutation get-or-create-current-gov-review-week [{:keys [gov-review-week/week]}]
                                        ;(action [env] )
  (remote [{:keys [ast state] :as env}]
          (let [GovReviewWeek (comp/registry-key->class :app.ui.projects/GovReviewWeek)]
            (-> env
                (m/returning GovReviewWeek)
                (m/with-target
                  (targeting/multiple-targets
                   (targeting/replace-at [:component/id :gov-review :gov-review/current-week])))))))


(defmutation submit-current-gov-review-week  [{:keys [gov-review-week] :as params}]
  (action [env]
          (js/console.log "TTTT" gov-review-week))
  (remote [{:keys [ast state] :as env}]
          (let [GovReviewWeek (comp/registry-key->class :app.ui.projects/GovReviewWeek)
                resource-id (get-in @state [:component/id :session :account/resource])]
            (-> env
                (m/with-params (merge params {:resource-id resource-id}))
                (m/returning GovReviewWeek)
                ))))


(defmutation get-or-create-gov-review-week [{:keys [gov-review-week/week index]}]
                                        ;(action [env] )
  (remote [{:keys [ast state] :as env}]
          (let [GovReviewWeek (comp/registry-key->class :app.ui.projects/GovReviewWeek)]
            (-> env
                (m/returning GovReviewWeek)
                (m/with-target
                  (targeting/replace-at [:component/id :gov-review :gov-review/current-weeks index]))))))




(defmutation populate-projects [_]
  (action [{:keys [state]}]
          (let [projects (get @state :projects)]
            
            
            (swap! state assoc-in [:component/id :resources :resources/projects] projects)
            (swap! state assoc  :projects []))))



(defmutation set-project-lead [{:keys [:project-info/id :lead-id]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-info/id id :project-info/project-lead]
                                       [:resource/id  lead-id])))))
  (remote [env] true))

(defmutation toggle-project-lead! [{:keys [val]}]
  (action [{:keys [state ref]}]
          (swap! state (fn [s]
                         (let [path (conj ref :ui/project-leads)
                               c-project-leads (or (get-in s path) #{})]
                           (assoc-in s  path (if (contains? c-project-leads val)
                                               (disj c-project-leads val)
                                               (conj c-project-leads val))))))))



(defmutation add-fluxod-project-names [{:keys [new-name project-info/id]}]
  (action [{:keys [state ref] :as env}]
          (js/console.log " NEW FORM TX " ref)
          (let [current-names (ns/get-in-graph @state (conj ref :project-info/fluxod-project-names))
                new-names (conj current-names new-name )
                current-names2 (if (nil? current-names) [] current-names)
                ]
            (js/console.log "NEW NAMES" current-names)
            (ns/update-caller! env assoc :ui/new-fluxod-project-name "")

            (swap! state (fn [s] (assoc-in s (conj ref  :project-info/fluxod-project-names) (conj current-names new-name))))))
  (remote [env] true))

(defn order-valid?
  "A user-written item validator (by field)"
  [{:order/keys [name days amount] :as order} field]
  (try
    (case field
      :order/name (boolean (seq name))
      :order/days (math/>= days 0)
      :order/amount (math/>= amount 0)
      true)
    (catch :default _
      false)))

(def order-validator (fs/make-validator order-valid?))


(defmutation remove-order [{:order/keys [id] :keys [finance]}]
  (action [{:keys [state]}]
          (swap! state (fn [s]
                         (-> s
                             (merge/remove-ident* [:order/id id] [:finance/id finance :finance/orders])
                             (update :order/id dissoc id))))))


(defmutation set-order-currency [{:keys [id value]}]
  (action [{:keys [state]}]
          (let [tasks (vals (get @state :task/id))]
            (swap! state assoc-in [:order/id id :order/currency] value)))

  (remote [env]
          true))

(defmutation save-order
  "Unchecked mutation. Sends the given diff to the server without checking validity. See try-save-item."
  [{:order/keys [id]
    :keys      [diff]
    :as        params}]
  (action [{:keys [app state]}]
          (js/console.log "id " id)
          (swap! state assoc-in [:order/id id :ui/saving?] true))
  (remote [env] true)
  (ok-action [{:keys [state tempid->realid] :as env}]
             (swap! state (fn [s]
                            (-> s
                                (update-in [:order/id id] assoc :ui/new? false :ui/saving? false)
                                (fs/entity->pristine* [:order/id id])))))
  (error-action [{:keys [state]}]
                (js/alert "Failed to save item")
                (swap! state (fn [s]
                               (-> s
                                   (update-in [:order/id id] assoc :ui/saving? false))))))










(defmutation try-save-order [{:order/keys [id]
                             :keys      [diff]
                             :as        params}]
  (action [{:keys [app state]}]
          (let [state-map       @state
                ident           [:order/id id]
                completed-state (fs/mark-complete* state-map ident)
                order            (get-in completed-state ident)
                Order    (comp/registry-key->class :app.ui.projects/Order)
                order-props      (fdn/db->tree (comp/get-query Order) order completed-state)
                valid?          (= :valid (order-validator order-props))]
            (if valid?
              (comp/transact! app [(save-order params)])
              (reset! state completed-state)))))

(defmutation set-project-status [{:keys [project-info/id status]}]

  (action [{:keys [state]}]
          (js/console.log "project panel id" id)
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-info/id id :project-info/status] status)))))
  (remote [env] true))

(defmutation set-project-entity [{:keys [project-info/id entity]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-info/id id :project-info/entity] entity)))))
  (remote [env] true))



(defmutation set-current-gov-week [{:keys [gov-review-week]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:component/id :gov-review :gov-review/current-week] [:gov-review-week/week (:gov-review-week/week gov-review-week)])))))
                                        ;(remote [env] true)
  )



(defmutation set-project-fluxod-name [{:keys [project-info/id name]}]
  (action [{:keys [state]}]
          (let [TeamCheckbox (comp/registry-key->class :app.ui.root/TeamCheckbox)]
            
            (swap! state (fn [state]
                           (-> state
                               (assoc-in [:project-info/id id :project-info/fluxod-name] name)
                               )))
            ;; (swap! state  merge/merge-component TeamCheckbox
            ;;        {:db/id team-id :team/name name }
            
            ;;        )
            ))
  (remote [env] true))






(defmutation set-project-phase [{:keys [project-info/id phase]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-info/id id :project-info/phase] phase)))))
  (remote [env] true))


(defmutation set-functional-lead [{:keys [:project-info/id :lead-id]}]
  (action [{:keys [state]}]
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:project-info/id id :project-info/functional-lead]
                                       [:resource/id  lead-id])))))
  (remote [env] true))

(defmutation set-technical-lead [{:keys [:project-info/id :lead-id]}]
  (action [{:keys [state component app ref] :as env}]
                                        ;(ns/)
          (let [ProjectInfo (comp/registry-key->class :app.ui.projects/ProjectInfo)]
            

            (mutations/set-props {:project-info/new "NEW2"})
            (js/console.log "ERF" ref)
                                        ;(swap! state update-in ref (fn [st] (merge st {:project-info/new "NEW"})))
            ;(ns/update-caller! env (fn [m ](merge m {:project-info/new "NEW"})))
            (js/console.log "VAL 1" (comp/get-query ProjectInfo))
            (js/console.log "VAL 2" (denormalize/db->tree [:project-info/id {:project-info/technical-lead [:resource/name]} ] [:project-info/id  #uuid "850d9f1e-27e1-e911-b19b-9cb6d0e1bd60"] @state))

            (swap! state (fn [state]
                          (-> state
                              (assoc-in [:project-info/id id :project-info/technical-lead]
                                        [:resource/id  lead-id]))))))
  (remote [env] true))







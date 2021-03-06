(ns app.model.team
  (:require
   [com.wsscode.pathom.connect :as pc]
   [app.model.database :refer [conn]]
   [clojure.set :as set]
   [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
   
   [datomic.api :as d]))


(pc/defresolver team-resolver [{:keys [db connection]} {:team/keys [name]}]
  {::pc/input  #{:team/name}
   ::pc/output [:team/name :team/type :db/id
                {:team/lead [:resource/name :resource/id :resource/email-address]}
                {:team/resources [:resource/name :resource/id :resource/email-address]} ]}
  
  (d/pull db [:db/id :team/name :team/type {:team/resources [:resource/name :resource/email-address :resource/id]} {:team/lead [:resource/name :resource/email-address :resource/id]} ] [:team/name name]))





(pc/defmutation set-team-name [{:keys [db connection]} {:keys [team-id name]}]
  {::pc/sym`set-team-name
   ::pc/params [:team-id :team-member-id]
   ::pc/output [:team/name]}
  (if (tempid/tempid? team-id)
    (let [tx @(d/transact connection [{:db/id "new"
                                       :team/name name}])
          new-id (get-in tx [:tempids "new"])]
      {:tempids {team-id new-id}})
    (do (d/transact connection [{:db/id team-id
                                 :team/name name}]
                    )
        {})))



(pc/defmutation set-team-type [{:keys [db connection]} {:keys [team-id type]}]
  {::pc/sym`set-team-type
   ::pc/params [:team-id :team-member-id]
   ::pc/output []}
  (let [ ]
    (d/transact connection [{:db/id team-id
                             :team/type type}])
    {}))





(pc/defmutation set-team-lead [{:keys [db connection]} {:keys [team-id lead-id]}]
  {::pc/sym`set-team-lead
   ::pc/params [:team-id :team-member-id]
   ::pc/output []}
  (let [ ]
    (d/transact connection [{:db/id team-id
                             :team/lead [:resource/id lead-id]}])
    {}))


#_(defmutation set-team-type [{:keys [team-id type]}]
  (action [{:keys [state]}]
          
          (swap! state (fn [state]
                         (-> state
                             (assoc-in [:team/id team-id :team/type] type)))))
  (remote [env] true))


(pc/defmutation add-team-member [{:keys [db connection]} {:keys [team-id team-member-id]}]
  {::pc/sym`add-team-member
   ::pc/params [:team-id :team-member-id]
   ::pc/output [:team/name]}
  (let [{:keys [team/resources team/name]} (d/pull db [:team/resources :team/name] 17592186109848)
        resources-added (conj resources [:resource/id team-member-id])]
    (d/transact connection [{:db/id team-id
                             :team/resources resources-added}])
    {:team/name name}))



(pc/defmutation delete-team-member [{:keys [db connection]} {:keys [team-id team-member-id]}]
  {::pc/sym`delete-team-member
   ::pc/params [:team-id :team-member-id]
   ::pc/output [:team/name]}
  (let [{:keys [team/resources team/name]} (d/pull db [:team/resources :team/name] 17592186109848)

        resources2 (map #(:db/id %) resources)
        resource-to-remove (d/q '[:find ?e . :in $ ?id :where [?e :resource/id ?id]] db team-member-id)
        ]
    (d/transact connection [[:db/retract team-id :team/resources resource-to-remove]])
    {:team/name name}))



(pc/defmutation delete-team [{:keys [db connection]} {:keys [team-id]}]
  {::pc/sym`delete-team
   ::pc/params [:team-id]
   ::pc/output []}
  (let [
        ]
    (d/transact connection [[:db/retractEntity team-id]])
    {}))


(pc/defresolver all-teams-resolver [{:keys [db connection]} _]
  {::pc/output [{:teams
                 [:team/name
                  :team/type
                  :db/id
                  
                  {:team/lead
                   [:resource/id
                    :resource/name
                    :resource/email-address
                    :resource/active?
                    :resource/profile]}
                  {:team/resources
                   [:resource/id
                    :resource/name
                    :resource/email-address
                    :resource/active?
                    :resource/profile]}
                   
                  ]}]
}
  {:teams (d/q  '[:find ?name
                  :keys team/name
                  :where
                  [?e :team/name ?name]
                  ] db)})



(def resolvers  [team-resolver all-teams-resolver add-team-member delete-team-member set-team-name set-team-type set-team-lead delete-team])

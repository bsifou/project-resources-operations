(ns run
  (:require ;[com.wsscode.pathom.connect :as pc]
                                        ; [taoensso.timbre :as log]






                                        ;[datomic.client.api :as d]
   [datomic.api :as d]
   [app.model.api :as api]



                                        ;[clojure.data.generators :as gen]

   [clojure.pprint :as pp]
                                        ;[app.model.database :as db :refer [conn]]
   [overtone.at-at :as at-at]
   [clojure.core :refer :all]
   [tick.alpha.api :as t])
  )



;; (defn run []
;;   (println "delete projects...........")
;;   (d/transact
;;    (d/connect "datomic:dev://localhost:4334/one2")

;;    (mapv
;;     (fn [id]

;;       [:db/retractEntity id])

;;     (d/q '[:find [?e ...]

;;            :where
;;            [?e :project/name ?n]

;;            ] (d/db (d/connect "datomic:dev://localhost:4334/one2")))))

;;   (println "seed projects...............")
;;   @(d/transact (d/connect "datomic:dev://localhost:4334/one2")  (get-all-projects))

;;   (println "Done....................")
;;   )



;(def my-pool (at-at/mk-pool))

;(at-at/every (t/millis (t/new-duration 120 :minutes)) #(println "I am cool!") my-pool :initial-delay (t/millis (t/new-duration 120 :minutes)))


;; #_





;; ;; ;(System/exit 0)

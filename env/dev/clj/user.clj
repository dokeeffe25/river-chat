(ns user
  (:require [luminus-migrations.core :as migrations]
            [river-chat.config :refer [env]]
            [mount.core :as mount]
            [river-chat.figwheel :refer [start-fw stop-fw cljs]]
            river-chat.core))

(defn start []
  (mount/start-without #'river-chat.core/repl-server))

(defn stop []
  (mount/stop-except #'river-chat.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))



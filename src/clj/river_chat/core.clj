(ns river-chat.core
  (:gen-class)
  (:require [cider.nrepl :refer [cider-nrepl-handler]]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.logging :as log]
            [luminus-migrations.core :as migrations]
            [luminus.http-server :as http]
            [luminus.repl-server :as repl]
            [mount.core :as mount]
            [river-chat.config :refer [env]]
            [river-chat.db.core :as db]
            [river-chat.handler :as handler]
            [river-chat.message :as message]))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(mount/defstate ^{:on-reload :noop}
                http-server
                :start
                (http/start
                  (-> env
                      (assoc :handler (handler/app))
                      (update :port #(or (-> env :options :port) %))))
                :stop
                (http/stop http-server))

(mount/defstate ^{:on-reload :noop}
                repl-server
                :start
                (when-let [nrepl-port (env :nrepl-port)]
                  (repl/start {:port nrepl-port :handler cider-nrepl-handler}))
                :stop
                (when repl-server
                  (repl/stop repl-server)))


(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (cond
    (some #{"init"} args)
    (do
      (mount/start #'river-chat.config/env)
      (migrations/init (select-keys env [:database-url :init-script]))
      (System/exit 0))
    (some #{"migrate" "rollback"} args)
    (do
      (mount/start #'river-chat.config/env)
      (migrations/migrate args (select-keys env [:database-url]))
      (System/exit 0))
    :else
    (start-app args)))


(ns river-chat.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[river-chat started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[river-chat has shut down successfully]=-"))
   :middleware identity})

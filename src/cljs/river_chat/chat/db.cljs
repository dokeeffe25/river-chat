(ns river-chat.chat.db
  (:require [cljs.spec.alpha :as s]))


(s/def ::page #{:chat})


(s/def ::db (s/keys :req-un [::page]))


(def default-db
  {:page :chat})

(ns river-chat.chat.db
  (:require [cljs.spec.alpha :as s]))


(s/def ::page #{:chat})
(s/def ::message-spec (s/keys :req-un [::message]))
(s/def ::messages (s/* ::message-spec))


(s/def ::db (s/keys :req-un [::page ::messages]))


(def default-db
  {:page :chat
   :messages []})

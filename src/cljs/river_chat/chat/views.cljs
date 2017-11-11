(ns river-chat.chat.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))


(defn chat-sidebar []
  [:div.col-md-3.col-xl-2
   "Sidebar"])


(defn message-list []
  (let [messages (rf/subscribe [:messages])]
    [:div.col-md-12
     [:ul
      (for [[i message] (map-indexed vector @messages)]
        ^{:key i}
        [:li (:message message)])]]))


(defn chat-input []
  (let [val (r/atom "")]
    (fn []
      [:div.col-md-10 {:id "message-entry"}
       [:input {:type "text"
                :placeholder "Message"
                :value @val
                :auto-focus true
                :on-change #(reset! val (-> % .-target .-value))
                :on-key-down #(case (.-which %)
                                13 (rf/dispatch [:chat/send-message @val])
                                nil)}]])))


(defn chat-window []
  [:div.col-md-9.col-xl-10
   [message-list]
   [chat-input]])


(defn chat-page []
  [:div.container-fluid
   [:div.row
    [chat-sidebar]
    [chat-window]]])
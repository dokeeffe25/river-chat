(ns ^:figwheel-no-load river-chat.app
  (:require [river-chat.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)

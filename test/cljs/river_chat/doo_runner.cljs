(ns river-chat.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [river-chat.core-test]))

(doo-tests 'river-chat.core-test)


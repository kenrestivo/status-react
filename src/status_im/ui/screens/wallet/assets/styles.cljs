(ns status-im.ui.screens.wallet.assets.styles
  (:require-macros [status-im.utils.styles :refer [defnstyle defstyle]])
  (:require [status-im.ui.components.styles :as styles]
            [status-im.ui.components.tabs.styles :as tabs.styles]
            [status-im.utils.platform :as platform]))

(defnstyle tab [active?]
           {:flex                1
            :height              tabs.styles/tab-height
            :justify-content     :center
            :align-items         :center
            :border-bottom-width (if active? 2 1)
            :border-bottom-color (if active?
                                   styles/color-blue4
                                   styles/color-gray10-transparent)})

(def tabs-container
  {:flexDirection :row})

(defnstyle tab-title [active?]
  {:ios        {:font-size 15}
   :android    {:font-size 14}
   :text-align :center
   :color      (if active?
                 styles/color-blue4
                 styles/color-black)})


;;TODO(goranjovic) - remove these as part of #2492

(def total-balance-container
  {:padding-top     20
   :padding-bottom  24
   :align-items     :center
   :justify-content :center})

(def total-balance
  {:flex-direction :row})

(def total-balance-value
  {:font-size 37
   :color     styles/color-white})

(defstyle total-balance-currency
  {:font-size   37
   :margin-left 9
   :color       styles/color-white-transparent-5
   :android     {:letter-spacing 1.5}
   :ios         {:letter-spacing 1.16}})

(def value-variation
  {:flex-direction :row
   :align-items    :center})

(defstyle value-variation-title
  {:font-size 14
   :color     styles/color-white-transparent-6
   :android   {:letter-spacing -0.18}
   :ios       {:letter-spacing -0.2}})
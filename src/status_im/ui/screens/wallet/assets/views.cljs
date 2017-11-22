(ns status-im.ui.screens.wallet.assets.views
  (:require-macros [status-im.utils.views :refer [defview letsubs]])
  (:require [clojure.string :as string]
            [re-frame.core :as re-frame]
            [status-im.ui.components.icons.vector-icons :as vector-icons]
            [status-im.ui.components.react :as react]
            [status-im.ui.components.status-bar :as status-bar]
            [status-im.ui.components.toolbar.view :as toolbar]
            [status-im.ui.components.toolbar.actions :as actions]
            [status-im.i18n :as i18n]
            [status-im.ui.components.styles :as component.styles]
            [status-im.utils.platform :as platform]
            [status-im.ui.screens.wallet.styles :as wallet.styles]
            [status-im.ui.screens.wallet.assets.styles :as styles]
            [status-im.ui.components.tabs.views :as tabs]))

(defn my-token-tab-title [active?]
  [react/text {:uppercase? true
               :style      (styles/tab-title active?)}
   [react/view {:style styles/total-balance-container}
    [react/view {:style styles/total-balance}
     [react/text {:style styles/total-balance-value} "abcd"]
     [react/text {:style styles/total-balance-currency} "SNT"]]
    [react/view {:style styles/value-variation}
     [react/text {:style styles/value-variation-title}
     "Total value"]
     #_[change-display change]]
    ]])

(defn my-token-tab-content []
  [react/view [react/text "My token stuff goes here"]])

(defn market-value-tab-title [active?]
  [react/text {:uppercase? true
               :style      (styles/tab-title active?)}
   "Market Value"])

(defn market-value-tab-content []
  [react/view [react/text "Market value stuff goes here"]])

(def tabs-list
  [{:view-id :wallet-my-token
    :content my-token-tab-title
    :screen  my-token-tab-content}
   {:view-id :wallet-market-value
    :content market-value-tab-title
    :screen  market-value-tab-content}])

(defview my-token-main []
  (letsubs [current-tab [:get :view-id]]
    [react/view {:style component.styles/flex}
     [status-bar/status-bar]
     [toolbar/toolbar {}
      toolbar/default-nav-back
      [toolbar/content-title "My Token"]]
     [tabs/swipable-tabs tabs-list current-tab true
      {:navigation-event     :navigation-replace
       :tab-style            styles/tab
       :tabs-container-style styles/tabs-container}]]))
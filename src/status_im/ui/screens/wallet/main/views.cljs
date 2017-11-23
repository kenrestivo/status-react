(ns status-im.ui.screens.wallet.main.views
  (:require-macros [status-im.utils.views :refer [defview letsubs]])
  (:require [clojure.string :as string]
            [re-frame.core :as rf]
            [status-im.ui.components.button.view :as btn]
            [status-im.ui.components.drawer.view :as drawer]
            [status-im.ui.components.list.views :as list]
            [status-im.ui.components.react :as react]
            [status-im.ui.components.icons.vector-icons :as vi]
            [status-im.ui.components.toolbar.view :as toolbar]
            [status-im.ui.components.toolbar.actions :as act]
            [status-im.i18n :as i18n]
            [status-im.react-native.resources :as resources]
            [status-im.utils.config :as config]
            [status-im.utils.ethereum.core :as ethereum]
            [status-im.utils.ethereum.tokens :as tokens]
            [status-im.utils.money :as money]
            [status-im.utils.platform :as platform]
            [status-im.utils.utils :as utils]
            [status-im.ui.screens.wallet.main.styles :as styles]
            [status-im.ui.screens.wallet.styles :as wallet.styles]
            [status-im.ui.components.styles :as components.styles]
            [status-im.ui.components.button.styles :as button.styles]
            [status-im.ui.screens.wallet.views :as wallet.views]))

(defn- show-not-implemented! []
  (utils/show-popup "TODO" "Not implemented yet!"))

(defn toolbar-title []
  [react/touchable-highlight {:on-press #(rf/dispatch [:navigate-to :wallet-list])}
   [react/view {:style styles/toolbar-title-container}
    [react/text {:style           styles/toolbar-title-text
                 :font            :toolbar-title
                 :number-of-lines 1}
     (i18n/label :t/main-wallet)]
    [vi/icon
     :icons/dropdown
     {:container-style styles/toolbar-title-icon
      :color           :white}]]])

(def transaction-history-action
  {:icon      :icons/transaction-history
   :icon-opts (merge {:color :white :style {:viewBox "-108 65.9 24 24"}} styles/toolbar-icon)
   :handler   #(rf/dispatch [:navigate-to :transactions-history])})

(defn toolbar-view []
  [toolbar/toolbar {:style wallet.styles/toolbar}
   [toolbar/nav-button (act/hamburger-white drawer/open-drawer!)]
   [toolbar/content-wrapper
    [toolbar-title]]
   [toolbar/actions
    [(assoc (act/opts [{:text (i18n/label :t/wallet-settings) :value show-not-implemented!}]) :icon-opts {:color :white})
     transaction-history-action]]])

(defn- change-display [change]
  (let [pos-change? (or (pos? change) (zero? change))]
    [react/view {:style (if pos-change?
                          styles/today-variation-container-positive
                          styles/today-variation-container-negative)}
     [react/text {:style (if pos-change?
                           styles/today-variation-positive
                           styles/today-variation-negative)}
      (if change
        (str (when pos-change? "+") change "%")
        "-%")]]))

(defn main-section [usd-value change syncing? error-message]
  [react/view {:style styles/main-section}
   (if syncing?
     wallet.views/wallet-syncing
     (when error-message
       wallet.views/error-message-view))
   [react/view {:style styles/total-balance-container}
    [react/view {:style styles/total-balance}
     [react/text {:style styles/total-balance-value} usd-value]
     [react/text {:style styles/total-balance-currency} (i18n/label :t/usd-currency)]]
    [react/view {:style styles/value-variation}
     [react/text {:style styles/value-variation-title}
      (i18n/label :t/wallet-total-value)]
     [change-display change]]
    [react/view {:style (merge button.styles/buttons-container styles/buttons)}
     [btn/button {:disabled? syncing?
                  :on-press #(rf/dispatch [:navigate-to :wallet-send-transaction])
                  :style    (button.styles/button-bar :first) :text-style styles/main-button-text}
      (i18n/label :t/wallet-send)]
     [btn/button {:disabled? syncing?
                  :on-press #(rf/dispatch [:navigate-to :wallet-request-transaction])
                  :style (button.styles/button-bar :other) :text-style styles/main-button-text}
      (i18n/label :t/wallet-request)]
     [btn/button {:disabled? true :style (button.styles/button-bar :last) :text-style styles/main-button-text}
      (i18n/label :t/wallet-exchange)]]]])

(defn- token->icon [{:keys [icon symbol]}]
  (case symbol
    "ETH" tokens/eth-icon
    ;; Tokens can define their own icons.
    ;; If not try to make one using a local image as resource, if it does not exist fallback to default.
    (or icon (tokens/make-icon (str "tokens/" (string/lower-case symbol) ".png")) tokens/default-icon)))

(defn add-asset []
  [list/touchable-item show-not-implemented!
   [react/view
    [list/item
     [list/item-icon {:icon :icons/add :style styles/add-asset-icon :icon-opts {:color :blue}}]
     [react/view {:style styles/asset-item-value-container}
      [react/text {:style styles/add-asset-text}
       (i18n/label :t/wallet-add-asset)]]]]])

(defn render-asset [{:keys [name symbol decimals amount] :as m}]
  (if name ;; If no 'name' then this the dummy value used to render `add-asset`
    [list/touchable-item #(utils/show-popup "TODO" (str "Details about " symbol " here"))
     [react/view
      [list/item
       (let [{:keys [source style]} (token->icon m)]
         [list/item-image source style])
       [react/view {:style styles/asset-item-value-container}
        [react/text {:style           styles/asset-item-value
                     :number-of-lines 1
                     :ellipsize-mode  :tail}
         (money/to-fixed (money/token->unit (or amount 0) decimals))]
        [react/text {:style           styles/asset-item-currency
                     :uppercase?      true
                     :number-of-lines 1}
         symbol]]
       [list/item-icon {:icon :icons/forward}]]]]
    [add-asset]))

(def ethereum-asset {:name "Ethereum" :symbol :ETH :decimals 18})

(defn tokens-for [network]
  (get tokens/all (ethereum/network network)))

(defn asset-section [network balance prices-loading? balance-loading?]
  (let [tokens (tokens-for network)
        assets (map #(assoc % :amount (get balance (:symbol %))) (concat [ethereum-asset] (when config/erc20-enabled? tokens)))]
    [react/view {:style styles/asset-section}
     [react/text {:style styles/asset-section-title} (i18n/label :t/wallet-assets)]
     [list/flat-list
      {:data       assets ;; TODO(jeluard) Reenable once we `add-an-asset` story is flecthed out ;; (concat assets [{}]) ;; Extra map triggers rendering for add-asset
       :render-fn  render-asset
       :on-refresh #(rf/dispatch [:update-wallet (when config/erc20-enabled? (map :symbol tokens))])
       :refreshing (boolean (or prices-loading? balance-loading?))}]]))

(defview wallet []
  (letsubs [network          [:network]
            balance          [:balance]
            portfolio-value  [:portfolio-value]
            portfolio-change [:portfolio-change]
            prices-loading?  [:prices-loading?]
            syncing?         [:syncing?]
            balance-loading? [:wallet/balance-loading?]
            error-message    [:wallet/error-message?]]
    [react/view {:style wallet.styles/wallet-container}
     [toolbar-view]
     [react/view components.styles/flex
      [main-section portfolio-value portfolio-change syncing? error-message]
      [asset-section network balance prices-loading? balance-loading?]]]))

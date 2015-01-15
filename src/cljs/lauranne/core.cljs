(ns lauranne.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defonce app-state (atom {:text "Hello Chestnut!"}))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (dom/div nil
            (dom/h1 nil (:text app))
            (dom/ul nil
              (dom/li nil
                (dom/label nil "Nimi: ")
                (dom/input #js {}))
              (dom/li nil
                (dom/label nil "Yritys: ")
                (dom/input #js {}))
              (dom/li nil
                (dom/label nil "Isäntä: ")
                (dom/input #js {}))
              (dom/li nil
                (dom/input #js {:type "button" :value "Kirjaudu"})))))))
    app-state
    {:target (. js/document (getElementById "app"))}))

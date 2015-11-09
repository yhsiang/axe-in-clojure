(ns axe.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]
            [clojure.data.json :as json]))

(def axe-url "http://axe-level-1.herokuapp.com/")

(def grades-keys [:國語 :數學 :自然 :社會 :健康教育])

(defn parse-table
  "return a list of rows"
  [url]
  (html/select
    (html/html-snippet
      (:body (client/get url)))
    [:table.table :tr]))

(defn list->map
  "return a sequence of map like {:國語 90 :數學 90 :自然 90 :社會 90 :健康教育 90}"
  [row]
  (reduce (fn [row-map [key val]]
            (assoc row-map key (Integer. val)))
          {}
          (map vector grades-keys row)))

(defn get-content
  "return map of content"
  [{:keys [content]}]
  content)

(defn list->str
  "convert sequence to vector of strings"
  [data-list]
  (map #(first (get-content %)) data-list))

;{:name "王小明", :grades {:國語 90, :數學 80, :自然 90, :社會 90, :健康教育 90}}
(defn parse-row
  "conver a row to a sequence of map"
  [row]
  (let [[first-row & rest-part] (list->str (filter map? (:content row)))]
  {:name first-row, :grades (list->map rest-part)}))

(defn -main
  "This will generate json from http://axe-level-1.herokuapp.com/"
  []
  (println (json/write-str (map parse-row (drop 1 (parse-table axe-url)))
    :escape-unicode false)))

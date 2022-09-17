(ns perf.core
  (:require [clojure-lsp.test-helper :as h]
            [clojure-lsp.queries :as q]
            [clojure.core.async :as async]))

(defn prepare-large-project []
  (async/go-loop []
    (async/<! (:current-changes-chan (h/components)))
    (recur))
  (async/go-loop []
    (async/<! (:diagnostics-chan (h/components)))
    (recur))
  (async/go-loop []
    (async/<! (:watched-files-chan (h/components)))
    (recur))
  (async/go-loop []
    (async/<! (:edits-chan (h/components)))
    (recur))
  (h/load-code-and-locs "(ns d.e.f) (def foo 1)" (h/file-uri "file:///b.clj"))
  (dotimes [cnt 2000]
    (h/load-code-and-locs (format "(ns d.e.u%d) (def u%d 1)" cnt cnt) (h/file-uri (format "file:///u%d.clj" cnt))))
  (let [code (str "(ns a.b.c (:require [d.e.f :as f-alias]))\n"
                  "(defn x [filename] filename |f-alias/foo)\n"
                  "x unknown unknown")]
    {:loc (let [[[row col]] (h/load-code-and-locs code)]
            {:row row :col col})
     :db (h/db)
     :components (h/components)}))

(defn find-definition [state]
  (q/find-definition-from-cursor (:db state) (h/file-path "/a.clj") (-> state :loc :row) (-> state :loc :col)))


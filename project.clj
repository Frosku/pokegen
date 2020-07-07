(defproject com.frosku/pokegen "0.1.0"
  :author "Frosku <frosku@frosku.com>"
  :signing {:gpg-key "frosku@frosku.com"}
  :description "Example using disultory to generate Pokemon"
  :url "http://github.com/Frosku/pokegen"
  :license {:name "The Unlicense"
            :url "https://www."}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "1.0.194"]
                 [com.frosku/disultory "0.1.4"]
                 [cheshire "5.10.0"]
                 [clj-http "3.10.1"]]
  :repl-options {:init-ns pokegen.core}
  :source-paths ["src"]
  :test-paths ["t"]
  :main pokegen.core
  :aot :all
  :target-path "target/%s/"
  :compile-path "%s/classes"
  :plugins [[lein-bump-version "0.1.6"]]
  :clean-targets ^{:protect false} [:target-path]
  :profiles {:dev {:dependencies [[clj-http-fake "1.0.3"]]}})

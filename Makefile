.EXPORT_ALL_VARIABLES:
.DEFAULT_GOAL := help
.PHONY: help dev test

SHELL = bash

include .env


define pprint
	@echo -e "$(PRIMARY_TEXT_COLOR)"
	@echo -e "$(LINES)"
	@echo -e $(1)
	@echo -e "$(LINES)"
	@echo -e "$(NORMAL_TEXT_COLOR)"
endef


help: ## Show help
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "$(PRIMARY_TEXT_COLOR)%-30s$(NORMAL_TEXT_COLOR) %s\n", $$1, $$2}' $(MAKEFILE_LIST)


clean: ## Clean
	$(call pprint, "Clean...")
	rm -rf .cljs_node_repl out target pom.xml


repl: ## Run REPL
	$(call pprint, "Run REPL...")
	clj -A:common:test-clj:test-cljs:repl


lint: ## Run linter
	$(call pprint, "Run linter...")
	clj-kondo --lint src:test/src:dev/src


test-cljs: ## Run ClojureScript tests
	$(call pprint, "Run ClojureScript tests...")
	clojure -A:common:test-cljs


test-clj: ## Run Clojure tests
	$(call pprint, "Run Clojure tests...")
	./bin/kaocha


test: test-clj test-cljs ## Run tests


jar: ## Build jar
	$(call pprint, "Build jar...")
	clojure -A:build
	clojure -A:version --pom --group-id ${GROUP_ID} --artifact-id ${ARTIFACT_ID} --scm-url ${SCM_URL}


install: ## Install locally
	$(call pprint, "Install locally...")
	clojure -A:install


deploy: ## Deploy to repository
	$(call pprint, "Deploy to repository...")
	clojure -A:deploy


init: ## Init first version
	$(call pprint, "Init first version...")
	git tag --annotate --message ${TAG_MSG} v0.0.1


patch: ## Increment patch version
	$(call pprint, "Increment patch version...")
	clojure -A:version patch --tag --message ${TAG_MSG}


minor: ## Increment minor version
	$(call pprint, "Increment minor version...")
	clojure -A:version minor --tag --message ${TAG_MSG}


major: ## Increment major version
	$(call pprint, "Increment major version...")
	clojure -A:version major --tag --message ${TAG_MSG}


minor-rc: ## Increment minor-rc version
	$(call pprint, "Increment minor-rc version...")
	clojure -A:version minor-rc --tag --message ${TAG_MSG}


major-rc: ## Increment major-rc version
	$(call pprint, "Increment major-rc version...")
	clojure -A:version major-rc --tag --message ${TAG_MSG}


minor-release: ## Increment minor-release version
	$(call pprint, "Increment minor-release version...")
	clojure -A:version minor-release --tag --message ${TAG_MSG}


major-release: ## Increment major-release version
	$(call pprint, "Increment major-release version...")
	clojure -A:version major-release --tag --message ${TAG_MSG}


release: ## Release a new version
	$(call pprint, "Release a new version...")
	git push origin --tags

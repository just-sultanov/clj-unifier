.EXPORT_ALL_VARIABLES:
.DEFAULT_GOAL := help
.PHONY: help dev test

SHELL = bash

include .env


help: ## Show help
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)


clean: ## Clean
	@echo "=================================================================="
	@echo "Clean..."
	@echo "=================================================================="
	rm -rf .cljs_node_repl out target pom.xml
	@echo -e "\n"


dev: ## Run REPL
	@echo "=================================================================="
	@echo "Run REPL..."
	@echo "=================================================================="
	clj -A:test-clj:test-cljs:dev


lint: ## Run linter
	@echo "=================================================================="
	@echo "Run linter..."
	@echo "=================================================================="
	clj-kondo --lint src:test/src
	@echo -e "\n"


test-cljs: ## Run ClojureScript tests
	@echo "=================================================================="
	@echo "Run ClojureScript tests..."
	@echo "=================================================================="
	clojure -A:test-cljs
	@echo -e "\n"


test-clj: ## Run Clojure tests
	@echo "=================================================================="
	@echo "Run Clojure tests..."
	@echo "=================================================================="
	./bin/kaocha
	@echo -e "\n"


test: test-clj test-cljs ## Run tests


jar: ## Build jar
	@echo "=================================================================="
	@echo "Build..."
	@echo "=================================================================="
	clojure -A:build
	clojure -A:version --pom --group-id ${GROUP_ID} --artifact-id ${ARTIFACT_ID} --scm-url ${SCM_URL}
	@echo -e "\n"


install: ## Install locally
	@echo "=================================================================="
	@echo "Install..."
	@echo "=================================================================="
	clojure -A:install
	@echo -e "\n"


deploy: ## Deploy to repository
	@echo "=================================================================="
	@echo "Deploy..."
	@echo "=================================================================="
	clojure -A:deploy
	@echo -e "\n"


init: ## Init first version
	git tag --annotate --message ${TAG_MSG} v0.0.1


patch: ## Increment patch version
	clojure -A:version patch --tag --message ${TAG_MSG}


minor: ## Increment minor version
	clojure -A:version minor --tag --message ${TAG_MSG}


major: ## Increment major version
	clojure -A:version major --tag --message ${TAG_MSG}


release: ## Release a new version
	@echo "=================================================================="
	@echo "Release..."
	@echo "=================================================================="
	git push origin --tags

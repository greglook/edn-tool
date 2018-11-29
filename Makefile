# Build file for edn-tool

default: package

.PHONY: clean test uberjar package

ifndef GRAAL_PATH
$(error GRAAL_PATH is not set)
endif

uberjar_path := target/uberjar/edn.jar
version := $(shell grep defproject project.clj | cut -d ' ' -f 3 | tr -d \")
platform := $(shell uname -s | tr '[:upper:]' '[:lower:]')
release_name := edn_$(version)_$(platform)

clean:
	rm -rf target dist edn

test:
	lein test

$(uberjar_path): src/*
	lein uberjar

uberjar: $(uberjar_path)

edn: $(uberjar_path) $(reflection-config)
	$(GRAAL_PATH)/bin/native-image \
	    --report-unsupported-elements-at-runtime \
	    -J-Xms3G -J-Xmx3G \
	    --no-server \
	    -jar $<

dist/$(release_name).tar.gz: edn
	@mkdir -p dist
	tar -cvzf $@ $^

package: dist/$(release_name).tar.gz

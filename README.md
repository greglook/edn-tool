EDN Tool
========

This project is a small command-line tool for parsing and pretty-printing EDN
data.


## Installation

Releases are published on the [GitHub project](https://github.com/greglook/edn-tool/releases).
The native binaries are self-contained, so to install them simply place them on
your path.


## Usage

The simplest way to use the tool is to pipe EDN to it on STDIN, which will
pretty-print each form to STDOUT.

```
$ echo "{:foo 123, :bar true}" | edn --width 10
{:bar true,
 :foo 123}
```


## Configuration

Under the hood, this tool uses [Puget](https://github.com/greglook/puget) to
pretty-print the output. The printer options may be customized by specifying a
configuration file with `--config` or dropping one in the default location in
your home directory. This should be an EDN file giving a map of printer options;
for example, to customize the coloring of `nil` values and enable namespaced
maps:

```clojure
{:namespace-maps true
 :color-scheme {:nil [:blue]}}
```


## License

This is free and unencumbered software released into the public domain.
See the UNLICENSE file for more information.

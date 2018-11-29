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


## License

This is free and unencumbered software released into the public domain.
See the UNLICENSE file for more information.

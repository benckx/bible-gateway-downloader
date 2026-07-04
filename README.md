# About

I wanted to be able to read Bible books on an epaper device but EPUB containing all books were impractical. I also
wanted to have the Wikipedia article in the same document to get some context.

So I made a small Kotlin CLI that scrapes a full Bible **book** from [BibleGateway](https://www.biblegateway.com/) and
exports it to a single **PDF** and **EPUB**. Each document also gets the book's **Wikipedia article** appended as an
appendix.

Entirely Vibe-coded with Claude Opus.

## Features

- Downloads every chapter of a book in one run, concatenated into one document.
- Outputs both a **PDF** and an **EPUB** (with a chapter index/TOC).
- Appends the cleaned **Wikipedia article** about the book to both files.
- Interactive picker with arrow navigation, type-to-filter and colors — or fully
  scriptable via command-line flags.
- Language-independent: books are looked up by OSIS code, so any supported
  translation returns its own localized text and book names.
- Embeds a Unicode font (GNU FreeSerif) so Greek/Hebrew terms render correctly in
  the PDF.

## Requirements

- JDK 21+

## Build

```bash
./gradlew build
```

This produces a runnable fat jar at `build/libs/bible-gateway-downloader-all.jar`.

## Usage

### Interactive mode

Run with no arguments to be prompted for version, testament and book:

```bash
./run.sh
# or
java -jar build/libs/bible-gateway-downloader-all.jar
```

`run.sh` builds the fat jar automatically if it is missing.

### Scripted mode

```bash
java -jar build/libs/bible-gateway-downloader-all.jar --version SG21 --book Ezek
```

| Option            | Description                                  | Default |
|-------------------|----------------------------------------------|---------|
| `-v`, `--version` | Bible version code (see below)               | —       |
| `-b`, `--book`    | Book OSIS code (e.g. `Ezek`) or English name | —       |
| `-s`, `--start`   | First chapter to download                    | `1`     |
| `-o`, `--out`     | Output directory                             | `out`   |

Files are written as `<book>-<version>.pdf` / `.epub` in the output directory.

## Supported versions

| Code   | Translation               |
|--------|---------------------------|
| `SG21` | Segond 21 (default)       |
| `LSG`  | Louis Segond              |
| `NIV`  | New International Version |
| `KJV`  | King James Version        |

I can add more on demand, I only needed those for myself. Don't hesitate to open an issue if you want a specific version
added.

## License

- Code: [MIT](LICENSE)
- Bundled font: GNU FreeFont (see `src/main/resources/fonts/FreeSerif-LICENSE.txt`)
- Bible text and Wikipedia content are the property of their respective owners; this
  tool is for personal use only.

## Distribution

You need to have Java installed to run the project. You can then just follow the standard process to build and run a
Gradle project (on either Windows, Linux or macOS). If you would like an easier distribution, don't hesiste to open an
issue on this repo.

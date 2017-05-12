#!/bin/bash

CWD="$PWD"
BUNDLE_DIR="$CWD/bundles"
BUNDLE_SCRIPT="$BUNDLE_DIR/bundle.sh"
FELIX_DIR="$CWD/felix"
FELIX_CACHE="${FELIX_DIR}/felix-cache/"
FELIX_BUNDLE_DIR="${FELIX_DIR}/bundle/"
DEPENDENT_BUNDLE_DIR="$CWD/dependent-bundles"

# Stop on error
set -e

# Remove existing bundles in felix
cd "$FELIX_BUNDLE_DIR"
echo "Removing all felix bundles in $FELIX_BUNDLE_DIR"
if [ "$(ls -A $FELIX_BUNDLE_DIR)" ]; then
  rm *.jar
fi
cd "$CWD"

# Remove felix cache
echo "Removing cache in $FELIX_CACHE"
if [ -d "$FELIX_CACHE" ]; then
  rm -R "$FELIX_CACHE"
fi

# Build and move new bundles
cd "$BUNDLE_DIR"
source "$BUNDLE_SCRIPT"
mv *.jar "$FELIX_BUNDLE_DIR"
cd "$CWD"

#cp dependent bundles
echo "Copy dependent bundles in $DEPENDENT_BUNDLE_DIR to $FELIX_BUNDLE_DIR"
cd "$DEPENDENT_BUNDLE_DIR"
cp *.jar "$FELIX_BUNDLE_DIR/"
cd "$CWD"

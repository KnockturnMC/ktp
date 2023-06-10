#!/usr/bin/env bash

POSITIONAL_ARGS=()
PAPER_HASH=""
PAPER_UPSTREAM_PATH=".gradle/caches/paperweight/upstreams/paper"
BRANCH="master"

function gitInPaper() {
    git --git-dir="$PAPER_UPSTREAM_PATH/.git" --work-tree="$PAPER_UPSTREAM_PATH" "$@"
}

while [[ $# -gt 0 ]]; do
    case $1 in
    -p | --paper)
        PAPER_HASH="$2"
        shift # past argument
        shift # past value
        ;;
    *)
        POSITIONAL_ARGS+=("$1") # save positional arg
        shift                   # past argument
        ;;
    esac
done

if [ -z "$PAPER_HASH" ]; then
    echo "Please provide a paper hash to update to!"
    exit 1
fi

# Updating the ref file
echo "Updating the paper reference"
sed -i "s#paperRef=.*#paperRef=$PAPER_HASH#g" gradle.properties

# Run gradle applyPatches
echo "Applying patches"
gradle applyPatches
EXIT=$?

if [ $EXIT -ne 0 ]; then
    echo "Failed to apply patches!"
    exit 1
fi

echo "Rebuilding patches"
gradle rebuildPatches

# Updating ktp paper repo
echo "Updating paper repo"
if [ "$(gitInPaper rev-parse --is-shallow-repository)" == "false" ]; then
    gitInPaper fetch --no-recurse-submodules origin "$BRANCH" 2>/dev/null
else
    gitInPaper fetch --no-recurse-submodules origin "$BRANCH" --unshallow 2>/dev/null
fi
gitInPaper gc 2>/dev/null

echo "Collecting commits since previous update"
LAST_REF="$(git show HEAD:gradle.properties | grep '^paperRef' | cut -d'=' -f2)"
COMMITS="$(gitInPaper log --format='%h %s' "$LAST_REF".."$PAPER_HASH")"

echo "Creating upstream update commit"
git add .
git commit -am "$( printf "Update upstream\n\n%s" "$COMMITS" )"

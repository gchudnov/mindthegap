#!/usr/bin/env bash
set -eu

SHADABLES=("primary" "grey" "blue" "green" "orange" "yellow" "red" "violet")
SHADES=("light" "dark")

# colors
declare -A COLORS
COLORS["light-primary"]="#c9d1d9"  # primary
COLORS["dark-primary"]="#0d1117"
COLORS["light-grey"]="#f5f5f5"     # grey
COLORS["dark-grey"]="#666666"
COLORS["light-blue"]="#dae8fc"     # blue
COLORS["dark-blue"]="#6c8ebf"
COLORS["light-green"]="#d5e8d4"    # green
COLORS["dark-green"]="#82b366"
COLORS["light-orange"]="#ffe6cc"   # orange
COLORS["dark-orange"]="#d79b00"
COLORS["light-yellow"]="#fff2cc"   # yellow
COLORS["dark-yellow"]="#d6b656"
COLORS["light-red"]="#f8cecc"      # red
COLORS["dark-red"]="#b85450"
COLORS["light-violet"]="#e1d5e7"   # violet
COLORS["dark-violet"]="#9673a6"

COLORS["white"]="#ffffff"          # white
COLORS["black"]="#000000"          # black

# values
declare -A VALUES
VARS=()
for key in "${!COLORS[@]}"; do
  VALUES[${COLORS[$key]}]=${key}
done

# inversions
declare -A INVERSIONS
INVERSIONS["black"]="light-primary"
INVERSIONS["white"]="dark-primary"

for name in "${SHADABLES[@]}"; do
  shade0=${SHADES[0]}
  shade1=${SHADES[1]}

  key0="${shade0}-${name}"
  key1="${shade1}-${name}"

  INVERSIONS["${key0}"]=${key1}
  INVERSIONS["${key1}"]=${key0}  
done

# preprocess SVG to normalize colors
npx svgo --multipass --config="./svgo.config.js" /home/gchudnov/Projects/mindthegap/res/domain.svg

# targets
TARGETS=()

# strokes
STROKES=("#ffffff")
for key in "${STROKES[@]}"; do
  [[ ! " ${TARGETS[*]} " =~ " ${key} " ]] && TARGETS+=("${key}")
done

# fills
FILLS=("#ffffff" "#000000")
for key in "${FILLS[@]}"; do
  [[ ! " ${TARGETS[*]} " =~ " ${key} " ]] && TARGETS+=("${key}")
done

# text-colors
TEXT_COLORS=()
for key in "${TEXT_COLORS[@]}"; do
  [[ ! " ${TARGETS[*]} " =~ " ${key} " ]] && TARGETS+=("${key}")
done

# border-colors
BORDER_COLORS=()
for key in "${BORDER_COLORS[@]}"; do
  [[ ! " ${TARGETS[*]} " =~ " ${key} " ]] && TARGETS+=("${key}")
done

# background-colors
BACKGROUND_COLORS=("#0d1117")
for key in "${BACKGROUND_COLORS[@]}"; do
  [[ ! " ${TARGETS[*]} " =~ " ${key} " ]] && TARGETS+=("${key}")
done

# variables
VARS=()
for target in "${TARGETS[@]}"; do
  name=${VALUES[${target}]}
  VARS+=("${name}")
done


# DEBUG - colors
# for key in "${!COLORS[@]}"; do echo "${key} => ${COLORS[$key]}"; done

# DEBUG - values
# for key in "${!VALUES[@]}"; do echo "${key} => ${VALUES[$key]}"; done

# DEBUG - inversions
# for key in "${!INVERSIONS[@]}"; do echo "${key} => ${INVERSIONS[$key]}"; done

# DEBUG - variables
# for key in "${!VARS[@]}"; do echo "${key} => ${VARS[$key]}"; done


# # create the header of the styles with variables
STYLES="<style type=\"text/css\">\n"
STYLES+=":root {\n"
for name in "${VARS[@]}"; do
  value=${COLORS[${name}]}
  color="--${name}-color"

  STYLES+="  ${color}: ${value};\n"
done
STYLES+="}\n"

# common styles
STYLES+="svg:target[style^=\"background-color:\"] { background-color: var(--dark-primary-color) !important; }\n"
STYLES+=":target g[filter=\"url(#dropShadow)\"] { filter: none !important; }\n\n"

# stroke
for value in "${STROKES[@]}"; do
  name=${VALUES[${value}]}
  inversion=${INVERSIONS[${name}]}

  inverse_var="--${inversion}-color"

  STYLES+=":target [stroke=${value}] { stroke: var(${inverse_var}); }\n"
done

# fills
for value in "${FILLS[@]}"; do
  name=${VALUES[${value}]}
  inversion=${INVERSIONS[${name}]}

  inverse_var="--${inversion}-color"

  STYLES+=":target [fill=${value}] { fill: var(${inverse_var}); }\n"
  STYLES+=":target g[fill=${value}] text { fill: var(${inverse_var}); }\n"
done

# text colors
for value in "${TEXT_COLORS[@]}"; do
  name=${VALUES[${value}]}
  inversion=${INVERSIONS[${name}]}

  inverse_var="--${inversion}-color"

  STYLES+=":target div[data-drawio-colors*=\"color: ${value}\"] div { color: var(${inverse_var}) !important; }\n"
done

# border-colors
for value in "${BORDER_COLORS[@]}"; do
  name=${VALUES[${value}]}
  inversion=${INVERSIONS[${name}]}

  inverse_var="--${inversion}-color"

  STYLES+=":target div[data-drawio-colors*=\"border-color: ${value}\"] { border-color: var(${inverse_var}) !important; }\n"
  STYLES+=":target div[data-drawio-colors*=\"border-color: ${value}\"] div { border-color: var(${inverse_var}) !important; }\n"
done

# background-colors
for value in "${BACKGROUND_COLORS[@]}"; do
  name=${VALUES[${value}]}
  inversion=${INVERSIONS[${name}]}

  inverse_var="--${inversion}-color"

  STYLES+=":target div[data-drawio-colors*=\"background-color: ${value}\"] { background-color: var(${inverse_var}) !important; }\n"
  STYLES+=":target div[data-drawio-colors*=\"background-color: ${value}\"] div { background-color: var(${inverse_var}) !important; }\n"
done

STYLES+="</style>\n"

printf "${STYLES}"

#!/usr/bin/env bash
set -exu

NAMES=("primary" "grey" "blue" "green" "orange" "yellow" "red" "violet")
SHADES=("light" "dark")

declare -A COLORS
COLORS[light-primary]=\#c9d1d9  # primary
COLORS[dark-primary]=\#0d1117
COLORS[light-grey]=\#f5f5f5     # grey
COLORS[dark-grey]=\#666666
COLORS[light-blue]=\#dae8fc     # blue
COLORS[dark-blue]=\#6c8ebf
COLORS[light-green]=\#d5e8d4    # green
COLORS[dark-green]=\#82b366
COLORS[light-orange]=\#ffe6cc   # orange
COLORS[dark-orange]=\#d79b00
COLORS[light-yellow]=\#fff2cc   # yellow
COLORS[dark-yellow]=\#d6b656
COLORS[light-red]=\#f8cecc      # red
COLORS[dark-red]=\#b85450
COLORS[light-violet]=\#e1d5e7   # violet
COLORS[dark-violet]=\#9673a6

COLORS[white]=\#ffffff          # white
COLORS[black]=\#000000          # black


# print the colors
# for key in "${!COLORS[@]}"; do echo "${key} => ${COLORS[$key]}"; done

# color keys to processs
KEYS=()
for name in "${NAMES[@]}"; do
  for shade in "${SHADES[@]}"; do
    key="${shade}-${name}"
    KEYS+=("${key}")
  done
done
KEYS+=("white" "black")

# create the header of the styles
STYLES="<style type=\"text/css\">\n"
STYLES+=":root {\n"
for key in "${KEYS[@]}"; do
  color="--${key}-color"
  STYLES+="  ${color}: ${COLORS[$key]};\n"
done
STYLES+="}\n\n"

# common styles
STYLES+="svg:target[style^=\"background-color:\"] { background-color: var(--dark-primary-color) !important; }\n"
STYLES+=":target g[filter=\"url(#dropShadow)\"] { filter: none !important; }\n\n"

# style per name
for name in "${NAMES[@]}"; do
  # todo: impl it
done

STYLES+="</style>\n"

printf "${STYLES}"

# :target [stroke="rgb(0, 0, 0)"] { stroke: var(--light-primary-color); }
# :target [stroke="rgb(255, 255, 255)"] { stroke: var(--dark-primary-color); }
# :target [fill="rgb(0, 0, 0)"] { fill: var(--light-primary-color); }
# :target [fill="rgb(255, 255, 255)"] { fill: var(--dark-primary-color); }
# :target g[fill="rgb(0, 0, 0)"] text { fill: var(--light-primary-color); }
# :target div[data-drawio-colors*="color: rgb(0, 0, 0)"] div { color: var(--light-primary-color) !important; }
# :target div[data-drawio-colors*="border-color: rgb(0, 0, 0)"] { border-color: var(--light-primary-color) !important; }
# :target div[data-drawio-colors*="border-color: rgb(0, 0, 0)"] div { border-color: var(--light-primary-color) !important; }
# :target div[data-drawio-colors*="background-color: rgb(255, 255, 255)"] { background-color: var(--dark-primary-color) !important; }
# :target div[data-drawio-colors*="background-color: rgb(255, 255, 255)"] div { background-color: var(--dark-primary-color) !important; }

# SVGO - first to convert hex
# shorthex
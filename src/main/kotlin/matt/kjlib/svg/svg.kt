package matt.kjlib.svg

/*
 * https://css-tricks.com/on-xlinkhref-being-deprecated-in-svg/
 * */
fun fixSVG(svg: String) = svg.replace("xlink:href", "href")
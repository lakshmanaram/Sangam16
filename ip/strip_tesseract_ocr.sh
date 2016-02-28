#!/bin/bash
image=${1-3.tif}

# Make everything that is nearly black go fully black, everything else goes white. Median for noise
# convert -delay 500 c1.jpg c2.jpg c3.jpg -normalize -fuzz 25% -fill black -opaque black -fuzz 0 -fill white +opaque black -median 9 out.gif
   convert "${image}" -normalize \
       -fuzz 40% -fill black -opaque black \
       -fuzz 0   -fill white +opaque black \
       -median 9 tmp_$$.png
      #4.tif - 60

# Get height of image - h
h=$(identify -format "%h" "${image}")

# Generate strips that are 40%, 30%, 20% and 10% of image height
for pc in 40 30 20 10; do
   # Calculate height of this strip in pixels - sh
   ((sh=(h*pc)/100))
   # Calculate offset from top of picture to top of bottom strip - omax
   ((omax=h-sh))
   # Calculate step size, there will be 20 steps
   ((step=omax/20))

   # Cut strips sh pixels high from the picture starting at top and working down in 20 steps
   for (( off=0;off<$omax;off+=$step)) do
      t=$(printf "%05d" $off)
      # Extract strip and resize to 80 pixels tall for tesseract
      convert tmp_$$.png -crop x${sh}+0+${off}      \
          -resize x80 -median 3 -median 3 \
          -threshold 90% +repage slice_${pc}_${t}.png

      # Run slice through tesseract, seeking sentences
      tesseract slice_${pc}_${t}.png temp
      # Now try and assess quality of output :-) ... by counting number of digits
      #digits=$(tr -cd "[0-9]" < temp.txt)
      #ndigits=${#digits}
      #[ $ndigits -gt 0 ] && [ $ndigits -lt 6 ] && echo $ndigits:$digits
   done
done | sort -n

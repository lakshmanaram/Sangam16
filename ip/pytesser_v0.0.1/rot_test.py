from PIL import Image
from pytesser import *

img = '/home/lakshmanaram/electronics_apps/sangam16/ip/4.jpg'
timg = '/home/lakshmanaram/electronics_apps/sangam16/ip/4.tif'
im = Image.open(img)
im = Image.open(timg)
#im = im.rotate(90)
#im.save(timg)
#im.show()
print image_to_string(im)
print "___________"
print image_file_to_string(timg)
print "___________"
print image_file_to_string(timg,graceful_errors = True)

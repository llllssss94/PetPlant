import numpy as np
import cv2
import glob
from matplotlib import pyplot as plt

speices = "sansevieria"
i = 1

for img in glob.glob("C://Project/my-pet-plant/image_classification_module/data/"+ speices +"/*.jpg"):
    cv_img = cv2.imread(img, cv2.IMREAD_GRAYSCALE)
    try:
        blur = cv2.GaussianBlur(cv_img, (5, 5), 0)
        ret, binary = cv2.threshold(blur, 0, 255, cv2.THRESH_BINARY+cv2.THRESH_OTSU)

        edge = cv2.Canny(binary, 127, 200)

        (_, contours, _) = cv2.findContours(edge, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
        mask = np.ones(cv_img.shape[:2], dtype="uint8") * 255
        
        # Draw the contours on the mask
        cv2.drawContours(mask, contours, -1, (0,255,0), 1)

        cv2.imwrite("C://Project/my-pet-plant/image_classification_module/contour/"+ speices +"/" + str(i) + ".jpg", mask)
        cv2.waitKey(0)
        print(i)
        i += 1
    except:
        print("err : " + str(i))
        continue

for img in glob.glob("C://Project/my-pet-plant/image_classification_module/data/"+ speices +"/*.jpeg"):
    cv_img = cv2.imread(img, cv2.IMREAD_GRAYSCALE)
    try:
        blur = cv2.GaussianBlur(cv_img, (5, 5), 0)
        ret, binary = cv2.threshold(blur, 0, 255, cv2.THRESH_BINARY+cv2.THRESH_OTSU)
        
        edge = cv2.Canny(binary, 100, 200)

        (_, contours, _) = cv2.findContours(edge, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
        mask = np.ones(cv_img.shape[:2], dtype="uint8") * 255
        
        # Draw the contours on the mask
        cv2.drawContours(mask, contours, -1, (0,255,0), 1)

        cv2.imwrite("C://Project/my-pet-plant/image_classification_module/contour/"+ speices +"/" + str(i) + ".jpg", mask)
        cv2.waitKey(0)
        print(i)
        i += 1
    except:
        print("err : " + str(i))
        continue

for img in glob.glob("C://Project/my-pet-plant/image_classification_module/data/"+ speices +"/*.png"):
    cv_img = cv2.imread(img, cv2.IMREAD_GRAYSCALE)
    try:
        blur = cv2.GaussianBlur(cv_img, (5, 5), 0)
        ret, binary = cv2.threshold(blur, 0, 255, cv2.THRESH_BINARY+cv2.THRESH_OTSU)
        
        edge = cv2.Canny(binary, 100, 200)

        (_, contours, _) = cv2.findContours(edge, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
        mask = np.ones(cv_img.shape[:2], dtype="uint8") * 255
        
        # Draw the contours on the mask
        cv2.drawContours(mask, contours, -1, (0,255,0), 1)

        cv2.imwrite("C://Project/my-pet-plant/image_classification_module/contour/"+ speices +"/" + str(i) + ".jpg", mask)
        cv2.waitKey(0)
        print(i)
        i += 1
    except:
        print("err : " + str(i))
        continue

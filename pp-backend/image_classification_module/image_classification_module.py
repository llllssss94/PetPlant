from keras.utils import np_utils
from keras.datasets import mnist
from keras.models import Sequential
from keras.layers import Dense, Activation
import numpy as np
from numpy import argmax
import cv2, os
from keras.preprocessing.image import ImageDataGenerator
from time import sleep

col_size = 56
row_size = 56

# 사용하려면 반드시 activate petplant ㄱㄱ
from keras.models import load_model
model = load_model("C:/Users/INNS-KSW/Desktop/my-pet-plant/image_classification_module/petplant_image.h5")

def main(filename = "", model = None):
    test_datagen = ImageDataGenerator(rescale=1./255)

    test_generator = test_datagen.flow_from_directory(
            'C:/Users/INNS-KSW/Desktop/my-pet-plant/image_classification_module/data/test/'+ filename,
            target_size=(col_size, row_size),    
            batch_size=16,
            class_mode=None,
            shuffle=False)

    # 6. 모델 사용하기
    print("-- Predict --")
    output = model.predict_generator(test_generator, steps=1)
    np.set_printoptions(formatter={'float': lambda x: "{0:0.3f}".format(x)})
    output = np.argmax(output,axis=-1)[0]

    if output == 0:
        print("This picture is Sansevieria")
        return "snake plant"
    elif output == 1:
        print("This picture is spartyfilm")
        return "spath"
    else:
        print("This picture is oxalis")
        return "oxalis"

if __name__ == "__main__":
    import sys
    name = sys.argv[1]
 
    print(main(filename=name, model=model))
    try:
        os.remove('C:/Users/INNS-KSW/Desktop/my-pet-plant/image_classification_module/data/test/'+ name + "/upload/" + name)
        os.rmdir('C:/Users/INNS-KSW/Desktop/my-pet-plant/image_classification_module/data/test/'+ name + "/upload")
        sleep(0.05)
        os.rmdir('C:/Users/INNS-KSW/Desktop/my-pet-plant/image_classification_module/data/test/'+ name)
    except:
        pass
# 0. 사용할 패키지 불러오기
import numpy as np
from keras.models import Sequential
from keras.layers import Dense
from keras.layers import Flatten, Dropout
from keras.layers.convolutional import Conv2D
from keras.layers.convolutional import MaxPooling2D
from keras.preprocessing.image import ImageDataGenerator
import cv2

col_size = 56
row_size = 56
output = 3
EPOCH = 50

def sigmoidal_decay(e, start=0, end=100, lr_start=1e-3, lr_end=1e-5):
    if e < start:
        return lr_start
    
    if e > end:
        return lr_end
    
    middle = (start + end) / 2
    s = lambda x: 1 / (1 + np.exp(-x))
    
    return s(13 * (-e + middle) / np.abs(end - start)) * np.abs(lr_start - lr_end) + lr_end

# 1. 데이터 생성하기
train_datagen = ImageDataGenerator(rescale=1./255, 
                                   rotation_range=10,
                                   width_shift_range=0.2,
                                   height_shift_range=0.2,
                                   shear_range=0.7,
                                   zoom_range=[0.9, 2.2],
                                   horizontal_flip=True,
                                   vertical_flip=True,
                                   fill_mode='nearest')

train_generator = train_datagen.flow_from_directory(
        './data/train',
        target_size=(col_size, row_size),
        batch_size=32,
        class_mode='categorical')

test_datagen = ImageDataGenerator(rescale=1./255)

test_generator = test_datagen.flow_from_directory(
        './data/validation',
        target_size=(col_size, row_size),    
        batch_size=32,
        class_mode='categorical')

# 2. 모델 구성하기
model = Sequential()

# Input Layer
model.add(Conv2D(32, kernel_size=(3, 3),
                 activation='relu',
                 input_shape=(col_size,row_size,3)))
model.add(MaxPooling2D(pool_size=(2, 2)))

# Hidden Layer
model.add(Conv2D(64, (3, 3), activation='relu'))
model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Conv2D(128, (3, 3), activation='relu'))
model.add(MaxPooling2D(pool_size=(2, 2)))

# Output Layer
model.add(Flatten())
model.add(Dense(256, activation='relu'))
model.add(Dense(output, activation='softmax'))

# 3. 모델 학습과정 설정하기
model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

from keras.callbacks import LearningRateScheduler, ModelCheckpoint
mc = ModelCheckpoint('weights.best.keras', monitor='val_acc', save_best_only=True)
lr = LearningRateScheduler(lambda e: sigmoidal_decay(e, end=EPOCH))


# 4. 모델 학습시키기
hist = model.fit_generator(
        train_generator,
        steps_per_epoch=15,
        epochs=EPOCH,
        validation_data=test_generator,
        validation_steps=5, 
        callbacks=[lr, mc])

# 5. 모델 평가하기
print("-- Evaluate --")
scores = model.evaluate_generator(test_generator, steps=5)
print("%s: %.2f%%" %(model.metrics_names[1], scores[1]*100))

# 6. 모델 사용하기
print("-- Predict --")
output = model.predict_generator(test_generator, steps=5)
np.set_printoptions(formatter={'float': lambda x: "{0:0.3f}".format(x)})
print(test_generator.class_indices)
print(output)

# 학습된 모델 저장
model.save('petplant_image.h5')

"""

# 표시
import matplotlib.pyplot as plt

fig, loss_ax = plt.subplots()

acc_ax = loss_ax.twinx()

loss_ax.plot(hist.history['loss'], 'y', label='train loss')
loss_ax.plot(hist.history['val_loss'], 'r', label='val loss')

acc_ax.plot(hist.history['acc'], 'b', label='train acc')
acc_ax.plot(hist.history['val_acc'], 'g', label='val acc')

loss_ax.set_xlabel('epoch')
loss_ax.set_ylabel('loss')
acc_ax.set_ylabel('accuray')

loss_ax.legend(loc='upper left')
acc_ax.legend(loc='lower left')

plt.show()




cv_img = cv2.imread('test.jpg', cv2.IMREAD_GRAYSCALE)
blur = cv2.GaussianBlur(cv_img, (5, 5), 0)
ret, binary = cv2.threshold(blur, 0, 255, cv2.THRESH_BINARY+cv2.THRESH_OTSU)

edge = cv2.Canny(binary, 50, 200)

(_, contours, _) = cv2.findContours(edge, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
mask = np.ones(cv_img.shape[:2], dtype="uint8") * 255

# Draw the contours on the mask
cv2.drawContours(mask, contours, -1, (0,255,0), 1)
cv2.imwrite("test.jpg", mask)

im = cv2.resize(cv2.imread('test.jpg'), (col_size, row_size)).astype(np.float32)
im[:,:,0] -= 103.939
im[:,:,1] -= 116.779
im[:,:,2] -= 123.68
im = np.expand_dims(im, axis=0)


out = model.predict(im)
print(out)
"""
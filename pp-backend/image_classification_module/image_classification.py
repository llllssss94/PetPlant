# 0. 사용할 패키지 불러오기
import numpy as np
from keras.optimizers import SGD
from keras.models import Sequential
from keras.layers import Dense, BatchNormalization
from keras.layers import Dropout
from keras.layers import Flatten
from keras.layers.convolutional import Conv2D
from keras.layers.convolutional import MaxPooling2D
from keras.preprocessing.image import ImageDataGenerator
import keras.backend.tensorflow_backend as K
import PIL
import cv2

col_size = 224
row_size = 224
output = 2
filter_pix = 3

# 랜덤시드 고정시키기
np.random.seed(3)

# 데이터셋 불러오기
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
        'C://Project/my-pet-plant/image_classification_module/data/train',
        target_size=(col_size, row_size),
        batch_size=8,
        class_mode='categorical')

test_datagen = ImageDataGenerator(rescale=1./255)

test_generator = test_datagen.flow_from_directory(
        'C://Project/my-pet-plant/image_classification_module/data/validation',
        target_size=(col_size, row_size),    
        batch_size=8,
        class_mode='categorical')

# 2. 모델 구성하기
with K.tf.device('/gpu:0'):
        model = Sequential()
        model.add(Conv2D(32, kernel_size=(filter_pix, filter_pix),
                        activation='relu',
                        input_shape=(col_size, row_size,3)))
        model.add(Conv2D(64, (filter_pix, filter_pix), activation='relu'))
        model.add(Conv2D(64, (filter_pix, filter_pix), activation='relu'))
        model.add(MaxPooling2D(pool_size=(2, 2)))
        model.add(Conv2D(128, (filter_pix, filter_pix), activation='relu'))
        model.add(Conv2D(128, (filter_pix, filter_pix), activation='relu'))
        model.add(MaxPooling2D(pool_size=(2, 2)))
        
        model.add(Conv2D(256, (filter_pix, filter_pix), activation='relu'))
        model.add(Conv2D(256, (filter_pix, filter_pix), activation='relu'))
        model.add(Conv2D(256, (filter_pix, filter_pix), activation='relu'))
        model.add(MaxPooling2D(pool_size=(2, 2)))
        model.add(Conv2D(512, (filter_pix, filter_pix), activation='relu'))
        model.add(Conv2D(512, (filter_pix, filter_pix), activation='relu'))
        model.add(Conv2D(512, (filter_pix, filter_pix), activation='relu'))
        model.add(MaxPooling2D(pool_size=(2, 2)))
        model.add(Conv2D(512, (filter_pix, filter_pix), activation='relu'))
        model.add(Conv2D(512, (filter_pix, filter_pix), activation='relu'))
        model.add(Conv2D(512, (filter_pix, filter_pix), activation='relu'))
        model.add(MaxPooling2D(pool_size=(2, 2)))
        

        model.add(Flatten())
        model.add(Dense(4096, activation='relu'))
        model.add(Dropout(0.5))
        model.add(Dense(4096, activation='relu'))
        model.add(Dropout(0.5))

        model.add(Dense(output, activation='softmax'))

        # 3. 모델 학습과정 설정하기
        model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

        # 4. 모델 학습시키기
        from keras.callbacks import EarlyStopping
        early_stopping = EarlyStopping() # 조기종료 콜백함수 정의

        hist = model.fit_generator(
                train_generator,
                steps_per_epoch=50,
                epochs=50,
                validation_data=test_generator,
                validation_steps=10,
                callbacks=[early_stopping])

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

"""
im = cv2.resize(cv2.imread('test.jpg'), (col_size, row_size)).astype(np.float32)
im[:,:,0] -= 103.939
im[:,:,1] -= 116.779
im[:,:,2] -= 123.68
im = np.expand_dims(im, axis=0)


out = model.predict(im)
print(out)
"""
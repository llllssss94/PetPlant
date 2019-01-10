import numpy as np                  # for working with tensors outside the network
import pandas as pd                 # for data reading and writing
import matplotlib.pyplot as plt     # for data inspection
from keras.layers import Input, Conv2D, Activation, BatchNormalization, GlobalAveragePooling2D, Dense, Dropout
from keras.layers.merge import add
from keras.activations import relu, softmax
from keras.models import Model
from keras import regularizers
from sklearn.preprocessing import OneHotEncoder
from sklearn.model_selection import train_test_split
from keras.preprocessing.image import ImageDataGenerator

col_size = 28
row_size = 28
output = 2

def block(n_output, upscale=False):
    # n_output: number of feature maps in the block
    # upscale: should we use the 1x1 conv2d mapping for shortcut or not
    
    # keras functional api: return the function of type
    # Tensor -> Tensor
    def f(x):
        
        # H_l(x):
        # first pre-activation
        h = BatchNormalization()(x)
        h = Activation(relu)(h)
        # first convolution
        h = Conv2D(kernel_size=3, filters=n_output, strides=1, padding='same', kernel_regularizer=regularizers.l2(0.01))(h)
        
        # second pre-activation
        h = BatchNormalization()(x)
        h = Activation(relu)(h)
        # second convolution
        h = Conv2D(kernel_size=3, filters=n_output, strides=1, padding='same', kernel_regularizer=regularizers.l2(0.01))(h)
        
        # f(x):
        if upscale:
            # 1x1 conv2d
            f = Conv2D(kernel_size=1, filters=n_output, strides=1, padding='same')(x)
        else:
            # identity
            f = x
        
        # F_l(x) = f(x) + H_l(x):
        return add([f, h])
    
    return f

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
        'C://Project/my-pet-plant/image_classification_module/data/train',
        target_size=(col_size, row_size),
        color_mode="grayscale",
        batch_size=32,
        class_mode='categorical',
        shuffle=True)

test_datagen = ImageDataGenerator(rescale=1./255)

test_generator = test_datagen.flow_from_directory(
        'C://Project/my-pet-plant/image_classification_module/data/validation',
        target_size=(col_size, row_size),
        color_mode="grayscale",    
        batch_size=32,
        class_mode='categorical',
        shuffle=True)

# input tensor is the 28x28 grayscale image
input_tensor = Input((col_size, row_size, 1))

# first conv2d with post-activation to transform the input data to some reasonable form
x = Conv2D(kernel_size=3, filters=16, strides=1, padding='same', kernel_regularizer=regularizers.l2(0.01))(input_tensor)
x = BatchNormalization()(x)
x = Activation(relu)(x)

# F_1
x = block(16)(x)
# F_2
x = block(16)(x)

# F_3
# H_3 is the function from the tensor of size 28x28x16 to the the tensor of size 28x28x32
# and we can't add together tensors of inconsistent sizes, so we use upscale=True
x = block(32, upscale=True)(x)       # !!! <------- Uncomment for local evaluation
# F_4
x = block(32)(x)                     # !!! <------- Uncomment for local evaluation
# F_5
x = block(32)(x)                     # !!! <------- Uncomment for local evaluation

# F_6
x = block(48, upscale=True)(x)       # !!! <------- Uncomment for local evaluation
# F_7
x = block(48)(x)                     # !!! <------- Uncomment for local evaluation

# last activation of the entire network's output
x = BatchNormalization()(x)
x = Activation(relu)(x)

# average pooling across the channels
# 28x28x48 -> 1x48
x = GlobalAveragePooling2D()(x)

# dropout for more robust learning
x = Dropout(0.2)(x)

# last softmax layer
x = Dense(units=output, kernel_regularizer=regularizers.l2(0.01))(x)
x = Activation(softmax)(x)

model = Model(inputs=input_tensor, outputs=x)
model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

from keras.callbacks import LearningRateScheduler, ModelCheckpoint
mc = ModelCheckpoint('weights.best.keras', monitor='val_acc', save_best_only=True)

def sigmoidal_decay(e, start=0, end=100, lr_start=1e-3, lr_end=1e-5):
    if e < start:
        return lr_start
    
    if e > end:
        return lr_end
    
    middle = (start + end) / 2
    s = lambda x: 1 / (1 + np.exp(-x))
    
    return s(13 * (-e + middle) / np.abs(end - start)) * np.abs(lr_start - lr_end) + lr_end

xs = np.linspace(0, 100)
ys = np.vectorize(sigmoidal_decay)(xs)
plt.plot(xs, ys)
plt.show()

EPOCHS = 100

lr = LearningRateScheduler(lambda e: sigmoidal_decay(e, end=EPOCHS))

hist = model.fit_generator(train_generator, epochs=EPOCHS, validation_data=test_generator, steps_per_epoch=15, callbacks=[lr, mc])

loss = hist.history['loss']
val_loss = hist.history['val_loss']
epochs = np.arange(1, EPOCHS + 1)

plt.plot(epochs, loss)
plt.plot(epochs, val_loss)
plt.show()

print("-- Evaluate --")
scores = model.evaluate_generator(test_generator, steps=5)
print("%s: %.2f%%" %(model.metrics_names[1], scores[1]*100))

from keras import models
from keras import layers
from keras import optimizers

model = models.Sequential()
model.add(layers.Dense(32, activation='relu', input_shape=(784,)))
model.add(layers.Dense(10, activation='softmax'))

#input_tensor = layers.Input(shape=(784,))
#x = layers.Dense(32, activation='relu')(input_tensor)
#output_tensor = layers.Dense(10, activation='softmax')(x)
#model = models.Model(inputs=input_tensor, outputs=output_tensor)

model.compile(optimizer=optimizers.RMSprop(lr=0.001),loss='mse',metrics=['accuracy'])

model.fit(inpu)
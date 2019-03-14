from keras import models
from keras.preprocessing import image
import numpy as np
import matplotlib.pyplot as plt
from utils import init_keras

init_keras()
model = models.load_model('cats_vs_dogs_small.h5')
model.summary()

image_path = r"C:\Users\huxiaomi\Downloads\deep-learning\data\kaggle-dogs-vs-cats\small\test\cats\cat.1700.jpg"
img = image.load_img(image_path, target_size=(150, 150))
img_tensor = image.img_to_array(img)
img_tensor = np.expand_dims(img_tensor, axis=0)
img_tensor /= 255.0

# plt.imshow(img_tensor[0])
# plt.show()

layers_output = [layer.output for layer in model.layers[:8]]
activation_model = models.Model(inputs=model.input, outputs=layers_output)
activations = activation_model.predict(img_tensor)

first_layer_activation = activations[0]
#plt.matshow(first_layer_activation[0, :, :, 20], cmap='viridis')
# plt.show()

layer_names = []
for layer in model.layers[:8]:
    layer_names.append(layer.name)

img_per_row = 16

for layer_name, activation in zip(layer_names, activations):
    if layer_name.find("conv2d") == -1:
        continue

    # the shape should be (N, height, width, FN)
    n_features = activation.shape[-1]
    feature_size = activation.shape[1]
    n_rows = n_features // img_per_row
    display_grid = np.zeros(
        (n_rows * feature_size, img_per_row * feature_size))

    for row in range(n_rows):
        for column in range(img_per_row):
            channel_image = activation[0, :, :, row*img_per_row+column]
            channel_image -= channel_image.mean()
            channel_image /= channel_image.std()
            channel_image *= 64
            channel_image += 128
            channel_image = np.clip(channel_image, 0, 255).astype(np.uint8)
            display_grid[row * feature_size:(row+1) * feature_size,
                         column * feature_size:(column+1) * feature_size] = channel_image

    scale = 1.0 / feature_size
    #plt.figure(
    #    figsize=(scale * display_grid.shape[1], scale * display_grid.shape[0]))    
    #plt.grid(False)
    plt.matshow(display_grid, aspect='auto', cmap='viridis')
    plt.title(layer_name)
    plt.show()

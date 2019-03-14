from keras import models
from keras.preprocessing import image
from keras.applications.vgg16 import VGG16, preprocess_input, decode_predictions
from keras import backend as K
import numpy as np
import matplotlib.pyplot as plt
import cv2
from utils import init_keras

init_keras()

img_path = r"C:\Users\huxiaomi\Downloads\deep-learning\data\kaggle-dogs-vs-cats\small\test\cats\cat.1502.jpg"
img = image.load_img(img_path, target_size=(224, 224))
x = image.img_to_array(img)
x = np.expand_dims(x, axis=0)
x = preprocess_input(x)

model = VGG16(weights='imagenet')
preds = model.predict(x)
print('Predicted:', decode_predictions(preds, top=3)[0])

idx_of_max = np.argmax(preds[0])
hit_output = model.output[:, idx_of_max]

last_conv_layer = model.get_layer('block5_conv3')

grads = K.gradients(hit_output, last_conv_layer.output)[0]
pooled_grads = K.mean(grads, axis=(0, 1, 2))

iterate = K.function([model.input], [pooled_grads, last_conv_layer.output[0]])
pooled_grads_value, last_conv_layer_output_value = iterate([x])

for i in range(512):
    last_conv_layer_output_value[:, :, i] *= pooled_grads_value[i]

heatmap = np.mean(last_conv_layer_output_value, axis=2)
heatmap = np.maximum(heatmap, 0)
heatmap /= np.max(heatmap)
plt.matshow(heatmap)
plt.show()

img = cv2.imread(img_path)
heatmap = cv2.resize(heatmap, (img.shape[1], img.shape[0]))
heatmap = np.uint8(255*heatmap)
heatmap = cv2.applyColorMap(heatmap, cv2.COLORMAP_JET)
superimposed_img = heatmap * 0.4 + img
cv2.imwrite(r'C:\Users\huxiaomi\Downloads\deep-learning\data\kaggle-dogs-vs-cats\heatmap.jpg', superimposed_img)
superimposed_img = np.uint8(superimposed_img)
plt.imshow(superimposed_img)
plt.show()
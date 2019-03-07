import os, shutil

def copy_files(fnames, source_folder, dest_folder):
    for fname in fnames:
        source_file = os.path.join(source_folder, fname)
        dest_file = os.path.join(dest_folder, fname)
        shutil.copyfile(source_file, dest_file)

base_dir=r"C:\Users\huxiaomi\Downloads\deep-learning\data\kaggle-dogs-vs-cats"
train_base_dir =  os.path.join(base_dir, "train")
test_base_dir =  os.path.join(base_dir, "test1")

small_base_dir = os.path.join(base_dir, "small")

dog_train_dir = os.path.join(small_base_dir, r"train\dogs")
os.makedirs(dog_train_dir,exist_ok=True)
cat_train_dir = os.path.join(small_base_dir, r"train\cats")
os.makedirs(cat_train_dir,exist_ok=True)

dog_cv_dir=os.path.join(small_base_dir, r"cv\dogs")
os.makedirs(dog_cv_dir,exist_ok=True)
cat_cv_dir=os.path.join(small_base_dir, r"cv\cats")
os.makedirs(cat_cv_dir,exist_ok=True)

dog_test_dir=os.path.join(small_base_dir, r"test\dogs")
os.makedirs(dog_test_dir,exist_ok=True)
cat_test_dir=os.path.join(small_base_dir, r"test\cats")
os.makedirs(cat_test_dir,exist_ok=True)

fnames = [f"cat.{i}.jpg" for i in range(1000)]
copy_files(fnames, train_base_dir, cat_train_dir)

fnames = [f"dog.{i}.jpg" for i in range(1000)]
copy_files(fnames, train_base_dir, dog_train_dir)

fnames = [f"cat.{i}.jpg" for i in range(1000,1500)]
copy_files(fnames, train_base_dir, cat_cv_dir)

fnames = [f"dog.{i}.jpg" for i in range(1000,1500)]
copy_files(fnames, train_base_dir, dog_cv_dir)

fnames = [f"cat.{i}.jpg" for i in range(1500,2000)]
copy_files(fnames, train_base_dir, cat_test_dir)

fnames = [f"dog.{i}.jpg" for i in range(1500,2000)]
copy_files(fnames, train_base_dir, dog_test_dir)


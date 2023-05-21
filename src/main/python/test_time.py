#read result.json
import json

with open("result.json", "r") as result_file:
    result = json.load(result_file)

time = result["resolution_time_distribution"]

# print a density plot of the resolution time distribution
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns


sns.distplot(time)

plt.show()
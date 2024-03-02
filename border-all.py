import requests
import os
import json
from enum import IntEnum



# 包含子区域   https://geo.datav.aliyun.com/areas_v3/bound/440000_full.json
# 不包含子区域 https://geo.datav.aliyun.com/areas_v3/bound/440000.json
path = 'https://geo.datav.aliyun.com/areas_v3/bound/'
# 文件夹
outputDir = '省市区-gcj02-2024'
# 开始id
startAdcode = '100000'
# 层级枚举 省-province 市-city 区-district
class Level(IntEnum):
  province = 1
  city = 2
  district = 3
# 结束层级
endLevel = 3

# 获取文件
def getFile (adcode, full, deep):
  # 请求头
  headers = {"User-Agent":"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.5383.400 QQBrowser/10.0.1313.400"}
  # 请求地址
  url = path + adcode
  if (full):
    url = url + '_full.json'
  else:
    url = url + '.json'
  res = requests.get(url, headers)
  # 保存文件
  saveFile(adcode, res.content)
  # 分析文件
  deep and analyzeFile(res.content)

# 保存文件
def saveFile (adcode, content):
  # 文件夹
  filePath = outputDir
  if not os.path.isdir(filePath):
    os.makedirs(filePath)
  # 文件名
  fileName = filePath + '/' + adcode + '.json'
  print('保存边框 -->', fileName)
  with open(fileName, 'wb') as f:
    # 保存文件
    f.write(content)
    f.close()


# 分析文件
def analyzeFile (content):
  contentJson = {}
  try:
    contentJson = json.loads(content)
    for area in contentJson['features']:
      # 判断层级
      if (Level[area['properties']['level']] > Level(endLevel)):
        return
      # 递归获取子级
      if (area['properties']['childrenNum'] > 0):
        getFile(str(area['properties']['adcode']), True, True)
      else:
        getFile(str(area['properties']['adcode']), False, False)
  except:
    print('没有子级 -->')


def run():
  getFile(startAdcode, True, True)


if __name__ == "__main__":
  run()
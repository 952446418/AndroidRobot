from collections import defaultdict
# import os
# import re
import jieba
# import codecs
from os.path import dirname, join

# 生成stopword表，需要去除一些否定词和程度词汇
stopwords = set()
filename = join(dirname(__file__), "停用词.txt")
fr = open(filename, "r", encoding='utf-8')
# fr = open(dirname(__file__),'停用词.txt', 'r', encoding='utf-8')
for word in fr:
    stopwords.add(word.strip())  # Python strip() 方法用于移除字符串头尾指定的字符（默认为空格或换行符）或字符序列。
# 读取否定词文件
# not_word_file = open('否定词.txt', 'r+', encoding='utf-8')
filename1 = join(dirname(__file__), "否定词.txt")
not_word_file = open(filename1, "r+", encoding='utf-8')
not_word_list = not_word_file.readlines()
not_word_list = [w.strip() for w in not_word_list]
# 读取程度副词文件
# degree_file = open('程度副词.txt', 'r+', encoding='utf-8')
filename2 = join(dirname(__file__), "程度副词.txt")
degree_file = open(filename2, "r+", encoding='utf-8')
degree_list = degree_file.readlines()
degree_list = [item.split(',')[0] for item in degree_list]
# 生成新的停用词表
filename3 = join(dirname(__file__), "stopwords.txt")
# with open('stopwords.txt', 'w', encoding='utf-8') as f:
with open(filename3, 'w', encoding='utf-8') as f:
    for word in stopwords:
        if (word not in not_word_list) and (word not in degree_list):
            f.write(word + '\n')


# jieba分词后去除停用词
def seg_word(sentence):
    seg_list = jieba.cut(sentence)
    seg_result = []
    for i in seg_list:
        seg_result.append(i)
    stopwords = set()
    # with open('stopwords.txt', 'r', encoding='utf-8') as fr:
    with open(filename3, 'r', encoding='utf-8') as fr:
        for i in fr:
            stopwords.add(i.strip())
    return list(filter(lambda x: x not in stopwords, seg_result))


# 找出文本中的情感词、否定词和程度副词
def classify_words(word_list):
    # 读取情感词典文件
    filename4 = join(dirname(__file__), "BosonNLP_sentiment_score.txt")
    sen_file = open(filename4, "r+", encoding='utf-8')
    # sen_file = open('BosonNLP_sentiment_score.txt', 'r+', encoding='utf-8')
    # 获取词典文件内容
    sen_list = sen_file.readlines()
    # 创建情感字典
    sen_dict = defaultdict()
    # 读取词典每一行的内容，将其转换成字典对象，key为情感词，value为其对应的权重
    for i in sen_list:
        if len(i.split(' ')) == 2:
            sen_dict[i.split(' ')[0]] = i.split(' ')[1]

    # 读取否定词文件
    not_word_file = open(filename1, 'r+', encoding='utf-8')
    not_word_list = not_word_file.readlines()
    # 读取程度副词文件
    degree_file = open(filename2, 'r+', encoding='utf-8')
    degree_list = degree_file.readlines()
    degree_dict = defaultdict()
    for i in degree_list:
        degree_dict[i.split(',')[0]] = i.split(',')[1]

    sen_word = dict()
    not_word = dict()
    degree_word = dict()
    # 分类
    for i in range(len(word_list)):
        word = word_list[i]
        if word in sen_dict.keys() and word not in not_word_list and word not in degree_dict.keys():
            # 找出分词结果中在情感字典中的词
            sen_word[i] = sen_dict[word]
        elif word in not_word_list and word not in degree_dict.keys():
            # 分词结果中在否定词列表中的词
            not_word[i] = -1
        elif word in degree_dict.keys():
            # 分词结果中在程度副词中的词
            degree_word[i] = degree_dict[word]

    # 关闭打开的文件
    sen_file.close()
    not_word_file.close()
    degree_file.close()
    # 返回分类结果
    return sen_word, not_word, degree_word


# 计算情感词的分数
def score_sentiment(sen_word, not_word, degree_word, seg_result):
    # 权重初始化为1
    W = 1
    score = 0
    # 情感词下标初始化
    sentiment_index = -1
    # 情感词的位置下标集合
    sentiment_index_list = list(sen_word.keys())
    # 遍历分词结果
    for i in range(0, len(seg_result)):
        # 如果是情感词
        if i in sen_word.keys():
            # 权重*情感词得分
            score += W * float(sen_word[i])
            # 情感词下标加一，获取下一个情感词的位置
            sentiment_index += 1
            if sentiment_index < len(sentiment_index_list) - 1:
                # 判断当前的情感词与下一个情感词之间是否有程度副词或否定词
                for j in range(sentiment_index_list[sentiment_index], sentiment_index_list[sentiment_index + 1]):
                    # 更新权重，如果有否定词，权重取反
                    if j in not_word.keys():
                        W *= -1
                    elif j in degree_word.keys():
                        W *= float(degree_word[j])


        # 定位到下一个情感词
        if sentiment_index < len(sentiment_index_list) - 1:
            i = sentiment_index_list[sentiment_index + 1]
        # print(score)
    return score


# 计算得分
def sentiment_score(sentence):
    # 1.对文档分词
    seg_list = seg_word(sentence)
    # 2.将分词结果转换成字典，找出情感词、否定词和程度副词
    sen_word, not_word, degree_word = classify_words(seg_list)
    # 3.计算得分
    score = score_sentiment(sen_word, not_word, degree_word, seg_list)
    return score
    # if score >0:
    #     print('心情良好')
    # elif score>-1:
    #     print('轻度不开心')
    # elif


# print("我今天很高兴也非常开心    ",sentiment_score("我今天很高兴也非常开心"))
# print('天灰蒙蒙的，路上有只流浪狗，旁边是破旧不堪的老房子   ',sentiment_score('天灰蒙蒙的，路上有只流浪狗，旁边是破旧不堪的老房子'))
# print('愤怒、悲伤和埋怨解决不了问题    ',sentiment_score('愤怒、悲伤和埋怨解决不了问题'))
# print('要每天都开心快乐    ',sentiment_score('要每天都开心快乐'))
# print('我不喜欢这个世界，我只喜欢你    ',sentiment_score('我不喜欢这个世界，我只喜欢你'))

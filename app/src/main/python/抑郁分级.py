from collections import defaultdict
import os
import re
import jieba
import codecs
from os.path import dirname, join

# 生成stopword表，需要去除一些否定词和程度词汇
stopwords = set()
filename = join(dirname(__file__), "停用词.txt")
fr = open(filename, "r", encoding='utf-8')
for word in fr:
    stopwords.add(word.strip())  # Python strip() 方法用于移除字符串头尾指定的字符（默认为空格或换行符）或字符序列。
# 读取否定词文件
filename1 = join(dirname(__file__), "否定词.txt")
not_word_file = open(filename1, "r+", encoding='utf-8')
not_word_list = not_word_file.readlines()
not_word_list = [w.strip() for w in not_word_list]
# 读取程度副词文件
filename2 = join(dirname(__file__), "程度副词.txt")
degree_file = open(filename2, "r+", encoding='utf-8')
degree_list = degree_file.readlines()
degree_list = [item.split(',')[0] for item in degree_list]
# 生成新的停用词表
filename3 = join(dirname(__file__), "stopwords.txt")
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
    with open(filename3, 'r', encoding='utf-8') as fr:
        for i in fr:
            stopwords.add(i.strip())
    return list(filter(lambda x: x not in stopwords, seg_result))


# 找出文本中的情感词、否定词和程度副词
def classify_words(word_list):
    # 读取情感词典文件
    filename4 = join(dirname(__file__), "BosonNLP_sentiment_score.txt")
    sen_file = open(filename4, "r+", encoding='utf-8')
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
    if score>10:
        score=1
    elif score>5:
        score=2
    elif score>-3:
        score=3
    else:
        score=4
    return score

# #1.主人，您现在感觉心情怎么样啊？
# print("今天心情很好。", sentiment_score("今天心情很好。今天心情很好"))
# print('今天心情还行。', sentiment_score('今天心情还行。今天心情还行'))
# print('今天心情不好。', sentiment_score('今天心情不好。今天心情不好'))
# print('今天心情很不好。', sentiment_score('今天心情很不好。今天心情很不好'))
# #2.主人，您今天早晨起床时候感觉心情如何？
# print("我心情很不错", sentiment_score("我心情很不错。我心情很不错"))
# print('我心情还行', sentiment_score('我心情还行。我心情还行'))
# print('我心情有些低落', sentiment_score('我心情有些低落。我心情有些低落'))
# print('有点莫名的悲伤', sentiment_score('有点莫名的悲伤。有点莫名的悲伤'))
# #3.（如果1是不开心）心疼主人，难过的话就哭出来吧，有乐乐陪在您身边。
# print('我不想哭',sentiment_score('我不想哭。我不想哭'))
# print('我不是很想哭',sentiment_score('我不是很想哭。我不是很想哭'))
# print('我想哭',sentiment_score('我想哭。我想哭'))
# print('我根本不想哭',sentiment_score('我根本不想哭。我根本不想哭'))
# #4.主人，您昨天晚上有睡好觉吗？一个好觉可以让主人开心起来哦！
# print('昨晚睡得很好',sentiment_score('昨晚睡得很好。昨晚睡得很好'))
# print('昨晚睡得还行',sentiment_score('昨晚睡得还行。昨晚睡得还行'))
# print('昨晚睡得不好',sentiment_score('昨晚睡得不好。昨晚睡得不好'))
# print('昨晚睡得很不好',sentiment_score('昨晚睡得很不好。昨晚睡得很不好'))
# #5.主人，您今天胃口咋样？
# print('今天胃口很好',sentiment_score('今天胃口很好。今天胃口很好'))
# print('今天胃口还行',sentiment_score('今天胃口还行。今天胃口还行'))
# print('今天胃口不好',sentiment_score('今天胃口不好。今天胃口不好'))
# print('今天胃口很不好',sentiment_score('今天胃口很不好。今天胃口很不好'))
# #6.今天有和小哥哥/小姐姐聊天吗？
# print('我今天找了好多人聊天',sentiment_score('我今天找了好多人聊天。我今天找了好多人聊天'))
# print('我今天找了几个人聊天',sentiment_score('我今天找了几个聊天。我今天找了几个聊天'))
# print('我今天没找人聊天',sentiment_score('我今天没找人聊天。我今天没找人聊天'))
# print('我今天没心情聊天',sentiment_score('我今天没心情聊天。我今天没心情聊天'))
# #7.今天体重有下降吗？不开心时候多吃点好吃的可以让主人开心！
# print("我体重下降了好多", sentiment_score("我体重下降了好多。我体重下降了好多"))
# print('我体重下降了了几斤', sentiment_score('我体重稍微下降了一些。我体重稍微下降了一些'))
# print('我体重下降不明显', sentiment_score('我体重下降不明显。我体重下降不明显'))
# print('我体重没有下降', sentiment_score('我体重没有下降。我体重没有下降'))
# #8.（偷偷问主人）今天主人的肠胃还好吗
# print('我的肠胃很好',sentiment_score('我的肠胃很好。我的肠胃很好'))
# print('我的肠胃还好',sentiment_score('我的肠胃还好。我的肠胃还好'))
# print('我的肠胃有点不舒服',sentiment_score('我的肠胃有点不舒服。我的肠胃有点不舒服'))
# print('我的肠胃确实有些不舒服',sentiment_score('我的肠胃确实有些不舒服。我的肠胃确实有些不舒服'))
# #9.主人今天有感觉心跳比平时快吗？
# print('没有啦，心跳是正常的',sentiment_score('没有啦，心跳是正常的。没有啦，心跳是正常的'))
# print('确实有点感觉心跳快，有些心慌',sentiment_score('确实有点感觉心跳快，有些心慌。确实有点感觉心跳快，有些心慌'))
# #10.主人感觉累吗？累了就好好休息一下，要注意身体呢！
# print('还好，没感觉到累',sentiment_score('还好，没感觉到累。还好，没感觉到累'))
# print('嗯嗯，确实有点累了',sentiment_score('嗯嗯，确实有点累了。嗯嗯，确实有点累了'))
# #11.主人今天感觉工作学习的效率如何？
# print('还不错，头脑很清晰',sentiment_score('还不错，头脑很清晰。还不错，头脑很清晰'))
# print('一般，感觉脑子有点混乱',sentiment_score('一般，感觉脑子有点混乱。一般，感觉脑子有点混乱'))
# print('很差，脑子像浆糊',sentiment_score('很差，脑子像浆糊。很差，脑子像浆糊'))
# #12.主人今天工作学习任务困难吗？
# print('特别简单',sentiment_score('特别简单。特别简单'))
# print('不算很难',sentiment_score('不算很难。不算很难'))
# print('今天任务好难啊，花了好久才做完',sentiment_score('今天任务好难啊，花了好久才做完。今天任务好难啊，花了好久才做完'))
# #13.主人今天感到烦躁吗？心平气和有助于身心健康哦！
# print('今天特开心',sentiment_score('今天特开心。今天特开心'))
# print('今天还好啦，没碰到啥烦心事',sentiment_score('今天还好啦，没碰到啥烦心事。今天还好啦，没碰到啥烦心事'))
# print('今天说好也好，说不好也不好',sentiment_score('今天说好也好，说不好也不好。今天说好也好，说不好也不好'))
# print('今天确实有点烦',sentiment_score('今天确实有点烦。今天确实有点烦'))
# #14.主人要开心哦，休息一下明天依然很美好呢！
# print('嗯嗯，感谢你了',sentiment_score('嗯嗯，感谢你了。嗯嗯，感谢你了'))
# # print('',sentiment_score(''))
# print('哎...明天还是那么苦...',sentiment_score('哎...明天还是那么苦...。哎...明天还是那么苦...'))
# #15.主人今天有遇到什么很生气的事情吗？
# print('没，今天都挺顺利的',sentiment_score('没，今天都挺顺利的。没，今天都挺顺利的'))
# print('还行吧！就像往常一样',sentiment_score('还行吧！就像往常一样。还行吧！就像往常一样'))
# print('有！我今天特别烦',sentiment_score('有！我今天特别烦。有！我今天特别烦'))
# #16.主人今天有做过什么决定吗？
# print('今天有做重要决定，我也很容易的解决问题了',sentiment_score('今天有做重要决定，我也很容易的解决问题了。今天有做重要决定，我也很容易的解决问题了'))
# print('今天一切正常，没做什么决定',sentiment_score('今天一切正常，没做什么决定。今天一切正常，没做什么决定'))
# print('有一个决策，但我拿捏不住，优柔寡断',sentiment_score('有一个决策，但我拿捏不住，优柔寡断。有一个决策，但我拿捏不住，优柔寡断'))
# #17.主人要相信自己！没有什么困难能够打倒我家主人！我家主人是最棒滴！
# print('嗯嗯，我是最棒的',sentiment_score('嗯嗯，我是最棒的。嗯嗯，我是最棒的'))
# print('哎...我不行...',sentiment_score('哎...我不行...。哎...我不行...'))
# #18.主人您的生活一定丰富多彩呢！要多关注人世间美好的瞬间哦！
# print('嗯嗯！这个我知道，这个世界本不缺少美，只是缺少发现美的眼睛',sentiment_score('嗯嗯！这个我知道，这个世界本不缺少美，只是缺少发现美的眼睛。嗯嗯！这个我知道，这个世界本不缺少美，只是缺少发现美的眼睛'))
# print('哎...生活一团糟',sentiment_score('哎...生活一团糟。哎...生活一团糟'))
# #19.主人您对周围的朋友伙伴亲人都十分重要！您是乐乐最重要的人！！！
# print('嗯嗯！乐乐也是我最重要的人之一',sentiment_score('嗯！乐乐也是我最重要的人之一。嗯嗯！乐乐也是我最重要的人之一'))
# print('你只是个机器人....我不重要',sentiment_score('你只是个机器人....我不重要。你只是个机器人....我不重要'))
# #20.主人您今天有坚持您的爱好吗？
# print('嗯嗯，我今天又打了一会代码',sentiment_score('嗯嗯，我今天又打了一会代码。嗯嗯，我今天又打了一会代码'))
# print('事情忙，顾不上...',sentiment_score('事情忙，顾不上...。事情忙，顾不上...'))
# print('今天情绪不好，不想玩',sentiment_score('今天情绪不好，不想玩。今天情绪不好，不想玩'))
## 简介

区块链作为一个公开的账本，自诞生以来便伴随着各种各样的安全监管问题。而NFT市场作为一个高度公开且自由的交易市场，也伴随着许许多多的监管难题。现有的区块链系统匿名监管协议存在泄露用户私钥以及开销过大的问题。本方案设计了一种基于区块链的可信匿名监管方案。该协议主要使用了密码学中双线性配对，以及伪公钥等技术实现。并且，我们分析了实验结果，发现本协议相比其他现存的方案无论在安全性还是在gas消耗上都有了显著的提升。因此，在NFT市场中嵌入本方案设计，可以做到有效监管NFT市场并且保护了用户的隐私。

## 参考

基于该[论文](https://github.com/Lee-TC/A2UA-protocol/blob/master/doc/paper.pdf)

## TODO

- [x] 系统初始化(Blockchain System Generate)

- [x] 用户注册(User Registration)

- [x] 监管机构初始化(TA Intailize)

- [ ] 监管/交易过程

- [ ] 动态评分(Dynamic Score)

- [ ] 环签名(Ring Signature)
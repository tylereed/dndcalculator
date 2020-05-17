setwd("C:/Users/tyler/OneDrive/Documents/r-workspace")
library(ggplot2)

d <- read.csv("special_advantage.csv")

ggplot(data = d, aes(x=AC, y=HitPercent, color=Type)) + geom_line() + geom_point()

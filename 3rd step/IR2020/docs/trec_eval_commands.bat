trec_eval -q -M 5 -m map -m num_rel_ret qrels_new_utf8.txt Newresults20.txt>answers/answers5.txt
trec_eval -q -M 10 -m map -m num_rel_ret qrels_new_utf8.txt Newresults20.txt>answers/answers10.txt
trec_eval -q -M 15 -m map -m num_rel_ret qrels_new_utf8.txt Newresults20.txt>answers/answers15.txt
trec_eval -q  -m map -m num_rel_ret qrels_new_utf8.txt Newresults20.txt>answers/answers20.txt
trec_eval -q -m map -m num_rel_ret qrels_new_utf8.txt Newresults30.txt>answers/answers30.txt
trec_eval -q  -m map -m num_rel_ret qrels_new_utf8.txt Newresults50.txt>answers/answers50.txt
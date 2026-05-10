# Tucil3_13524052_13524062 — Ice Sliding Puzzle Solver

## Deskripsi Program

**Ice Sliding Puzzle Solver** adalah program berbasis Java + Swing untuk menyelesaikan teka-teki *ice sliding puzzle*. Pada teka-teki ini, sebuah karakter (`Z`) meluncur di atas papan es dan akan terus bergerak ke arah yang dipilih sampai menabrak dinding (`X`) atau batas papan. Tujuannya adalah mencapai *goal* (`O`) setelah menyentuh seluruh *checkpoint* bernomor (`0`, `1`, `2`, ...) secara berurutan, dengan total biaya seminimal mungkin.

Program menyediakan empat algoritma pencarian:

- **UCS** (Uniform Cost Search)
- **A\*** (A-Star)
- **GBFS** (Greedy Best-First Search)
- **DFS** (Depth-First Search)

Untuk algoritma berbasis heuristik (A* dan GBFS), tersedia empat pilihan heuristik berbasis *checkpoint*:

- **H1** : Manhattan Distance
- **H2** : Euclidean Distance
- **H3** : Chebyshev Distance
- **H4** : Minkowski Distance (p = 3)

## Requirement

- **Java JDK 11** atau lebih baru
- **Apache Maven 3.6+**
- Sistem operasi: Windows / Linux / macOS (program telah dikembangkan dan diuji di Windows 11)


## Cara Kompilasi dan Menjalankan

```bash
mvn compile exec:java
```

## Author

| NIM | Nama |
|-----|------|
| 13524052 | Raynard Fausta |
| 13524062 | Nathan Edward Christofer Marpaung |

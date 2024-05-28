package com.merkletree;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MerkleTree {
    private List<String> dataBlocks;
    private String merkleRoot;

    // 构造方法，接受数据块列表
    public MerkleTree(List<String> dataBlocks) {
        if (dataBlocks == null || dataBlocks.isEmpty()) {
            throw new IllegalArgumentException("数据块列表不能为空");
        }
        this.dataBlocks = dataBlocks;
        this.merkleRoot = buildMerkleTree(dataBlocks);
    }

    // 构建Merkle树并返回根哈希值
    private String buildMerkleTree(List<String> dataBlocks) {
        List<String> currentLevel = new ArrayList<>();

        // 初始层：叶子节点，计算每个数据块的哈希值
        for (String block : dataBlocks) {
            currentLevel.add(Hash(block));
        }

        // 构建树，直到只剩下一个根哈希值
        while (currentLevel.size() > 1) {
            List<String> nextLevel = new ArrayList<>();

            // 两两配对计算父节点哈希值
            for (int i = 0; i < currentLevel.size(); i += 2) {
                if (i + 1 < currentLevel.size()) {
                    nextLevel.add(Hash(currentLevel.get(i) + currentLevel.get(i + 1)));
                } else {
                    // 处理奇数个节点，复制最后一个节点的哈希值
                    // nextLevel.add(currentLevel.get(i));
                    nextLevel.add(Hash(currentLevel.get(i) + currentLevel.get(i)));
                }
            }

            // 更新当前层为下一层
            currentLevel = nextLevel;
        }

        // 返回根哈希值
        return currentLevel.get(0);
    }

    // 获取Merkle根哈希值
    public String getMerkleRoot() {
        return this.merkleRoot;
    }

    // 示例Hash方法实现（使用SHA-256）
    private static String Hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // 搜索特定数据块并返回从数据块到Merkle根的路径
    public List<String> searchPath(String dataBlock) {
        String targetHash = Hash(dataBlock);
        List<String> path = new ArrayList<>();
        if (!dataBlocks.contains(dataBlock)) {
            return null;  // 数据块不在原始列表中
        }
        path.add(targetHash);  // 添加目标数据块的哈希

        List<String> currentLevel = new ArrayList<>();
        for (String block : dataBlocks) {
            currentLevel.add(Hash(block));
        }

        while (currentLevel.size() > 1) {
            List<String> nextLevel = new ArrayList<>();
            for (int i = 0; i < currentLevel.size(); i += 2) {
                String left = currentLevel.get(i);
                String right = (i + 1 < currentLevel.size()) ? currentLevel.get(i + 1) : left; // node基偶數考慮
                String parentHash = Hash(left + right);
                nextLevel.add(parentHash);

                if (left.equals(targetHash) || right.equals(targetHash)) {
                    path.add(parentHash);
                    targetHash = parentHash;  // 更新目标哈希为父节点哈希
                }
            }
            currentLevel = nextLevel;
        }
        return path;
    }

    // 搜索特定数据块是否存在
    public boolean search(String dataBlock) {
        if (dataBlock == null || dataBlock.isEmpty()) {
            return false;
        }

        String targetHash = Hash(dataBlock);
        List<String> currentLevel = new ArrayList<>();
        for (String block : dataBlocks) {
            currentLevel.add(Hash(block));
        }

        while (currentLevel.size() > 1) {
            List<String> nextLevel = new ArrayList<>();
            for (int i = 0; i < currentLevel.size(); i += 2) {
                String left = currentLevel.get(i);
                String right = (i + 1 < currentLevel.size()) ? currentLevel.get(i + 1) : left;
                String parentHash = Hash(left + right);
                nextLevel.add(parentHash);

                // 如果目标哈希匹配当前层的任何一个哈希，继续向上查找
                if (left.equals(targetHash) || right.equals(targetHash)) {
                    targetHash = parentHash;
                }
            }
            currentLevel = nextLevel;
        }

        // 最终验证是否根哈希匹配目标哈希
        return targetHash.equals(merkleRoot);
    }

    // 主方法，测试Merkle树的实现
    public static void main(String[] args) {
        List<String> dataBlocks = new ArrayList<>();
        dataBlocks.add("76592b9de6d38238a52a3651867871e5c670e6320a8ef46a84b5590f8933f33e");
        dataBlocks.add("7aa5ae9ecf82f6fc5208ecd0940fd71d50cd4ac596fccac2ede05af806a87b6e");
        dataBlocks.add("8f455bba4522752c63bbcd2b6d4c954e66a2bade8792dbe67be7c088affa422e");
        // dataBlocks.add("30fc9e3356c21227177859ff28b144526d9bcaf9802b1bc19ed5709ca482f8ae");

        MerkleTree merkleTree = new MerkleTree(dataBlocks);
        System.out.println("Merkle Root: " + merkleTree.getMerkleRoot());

        // 搜索特定数据块并输出路径
        String searchBlock = "76592b9de6d38238a52a3651867871e5c670e6320a8ef46a84b5590f8933f33e";
        /* 
        List<String> path = merkleTree.searchPath(searchBlock);
        if (path != null) {
            System.out.println("Path for " + searchBlock + ": " + path);
        } else {
            System.out.println(searchBlock + " not found in the Merkle Tree.");
        }
        */
        System.out.println(merkleTree.search(searchBlock));
    }
}

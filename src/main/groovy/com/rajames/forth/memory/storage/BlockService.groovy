/*
 * Copyright 2024 Robert A. James
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rajames.forth.memory.storage

import com.rajames.forth.ForthException
import com.rajames.forth.ForthRepl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BlockService {

    public static final int BLOCK_SIZE = 1024
    BlockRepository blockRepository

    @Autowired
    BlockService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository
    }

    @Autowired
    ForthRepl forthRepl

    @Transactional
    Block save(Block block) {
        forthRepl.BLK = block.blockNumber
        return blockRepository.save(block)
    }

    @Transactional
    Block getBlock(Integer blockNumber) {
        verifyBlockNumber(blockNumber)
        forthRepl.BLK = blockNumber
        Optional<Block> blockOptional = blockRepository.findByBlockNumber(blockNumber)

        if (blockOptional.isPresent()) {
            return blockOptional.get()
        } else {
            Block newBlock = new Block()
            byte[] newBytes = new byte[BLOCK_SIZE]
            Arrays.fill(newBytes as Byte[], null)

            newBlock.setBlockNumber(blockNumber)
            newBlock.setBytes(newBytes)
            return blockRepository.save(newBlock)
        }
    }

    @Transactional
    Block getBlock(Integer blockNumber, Boolean flag) {
        blockNumber = 0 - (blockNumber + BLOCK_SIZE)
        forthRepl.BLK = blockNumber
        if (flag) {
            Optional<Block> blockOptional = blockRepository.findByBlockNumber(blockNumber)

            if (blockOptional.isPresent()) {
                return blockOptional.get()
            } else {
                Block newBlock = new Block()
                byte[] newBytes = new byte[BLOCK_SIZE]
                Arrays.fill(newBytes as Byte[], null)

                newBlock.setBlockNumber(blockNumber)
                newBlock.setBytes(newBytes)
                return blockRepository.save(newBlock)
            }
        } else {
            throw new ForthException("Illegal memory access :(")
        }
    }

    @Transactional
    Block putBlock(Integer blockNumber) {
        verifyBlockNumber(blockNumber)
        forthRepl.BLK = blockNumber
        // Check if block with given blockNumber already exists
        Optional<Block> blockOptional = blockRepository.findByBlockNumber(blockNumber)

        Block block = blockOptional.orElseGet(Block::new)
        byte[] newBytes = new byte[BLOCK_SIZE]
        Arrays.fill(newBytes as Byte[], null)

        block.setBlockNumber(blockNumber)
        block.setBytes(newBytes)

        return blockRepository.save(block)
    }

    @Transactional
    Block putBlock(Integer blockNumber, Boolean flag) {
        blockNumber = 0 - (blockNumber + BLOCK_SIZE)
        forthRepl.BLK = blockNumber
        if (flag) {
            // Check if block with given blockNumber already exists
            Optional<Block> blockOptional = blockRepository.findByBlockNumber(blockNumber)

            Block block = blockOptional.orElseGet(Block::new)
            byte[] newBytes = new byte[BLOCK_SIZE]
            def fill = Arrays.fill(newBytes as Byte[], null)

            block.setBlockNumber(blockNumber)
            block.setBytes(fill)

            return blockRepository.save(block)
        } else {
            throw new ForthException("Illegal memory access :(")
        }
    }

    @Transactional
    Byte fetch(Integer address) {
        Integer blockNumber = (address / BLOCK_SIZE) as Integer
        forthRepl.BLK = blockNumber
        Integer index = address % BLOCK_SIZE

        // Find block or create new one if it doesn't exist
        Block block = blockRepository.findByBlockNumber(blockNumber)
                .orElseGet(() -> {
                    byte[] newBytes = new byte[BLOCK_SIZE]
                    Arrays.fill(newBytes as Byte[], null)

                    Block newBlock = new Block()
                    newBlock.setBlockNumber(blockNumber)
                    newBlock.setBytes(newBytes)

                    return blockRepository.save(newBlock)
                })

        return block.getBytes()[index]
    }

    @Transactional
    Byte fetch(Integer address, Boolean flag) {
        if (flag) {
            Integer blockNumber = (address / BLOCK_SIZE) - BLOCK_SIZE as Integer
            forthRepl.BLK = blockNumber
            Integer index = address % BLOCK_SIZE
            int absNumber = Math.abs(index) // <-- Always a positive number

            // Find block or create new one if it doesn't exist
            Block block = blockRepository.findByBlockNumber(blockNumber)
                    .orElseGet(() -> {
                        byte[] newBytes = new byte[BLOCK_SIZE]
                        Arrays.fill(newBytes as Byte[], null)

                        Block newBlock = new Block()
                        newBlock.setBlockNumber(blockNumber)
                        newBlock.setBytes(newBytes)

                        return blockRepository.save(newBlock)
                    })
            return block.getBytes()[absNumber]
        } else {
            throw new ForthException("Illegal memory access :(")
        }
    }

    @Transactional
    void store(Integer address, Byte value) {
        int blockNumber = address / BLOCK_SIZE as Integer
        forthRepl.BLK = blockNumber
        int index = address % BLOCK_SIZE

        // Find block or create new one if it doesn't exist
        Block block = blockRepository.findByBlockNumber(blockNumber)
                .orElseGet(() -> {
                    byte[] newBytes = new byte[BLOCK_SIZE]
                    Arrays.fill(newBytes as Byte[], null)

                    Block newBlock = new Block()
                    newBlock.setBlockNumber(blockNumber)
                    newBlock.setBytes(newBytes)

                    return blockRepository.save(newBlock)
                })

        byte[] bytes = block.getBytes()
        bytes[index] = value

        block.setBytes(bytes)
        blockRepository.save(block)
    }

    @Transactional
    void store(Integer address, Byte value, Boolean flag) {
        Integer blockNumber = (address / BLOCK_SIZE) - BLOCK_SIZE as Integer
        forthRepl.BLK = blockNumber
        Integer index = address % BLOCK_SIZE
        index = Math.abs(index) // <-- Always a positive number

        if (flag) {
            // Find block or create new one if it doesn't exist
            Block block = blockRepository.findByBlockNumber(blockNumber)
                    .orElseGet(() -> {
                        byte[] newBytes = new byte[BLOCK_SIZE]
                        Arrays.fill(newBytes as Byte[], null)

                        Block newBlock = new Block()
                        newBlock.setBlockNumber(blockNumber)
                        newBlock.setBytes(newBytes)

                        return blockRepository.save(newBlock)
                    })

            byte[] bytes = block.getBytes()
            bytes[index] = value

            block.setBytes(bytes)
            blockRepository.save(block)
        } else {
            throw new ForthException("Illegal memory access :(")
        }
    }

    private void verifyBlockNumber(Integer blockNumber) {
        if (blockNumber < 1) {
            throw new ForthException("Invalid block number: " + blockNumber)
        }
    }
}

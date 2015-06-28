package org.commonmark.internal;

import org.commonmark.node.Block;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.SourcePosition;
import org.commonmark.parser.BlockContinue;
import org.commonmark.parser.BlockStart;

public class BlockQuoteParser extends AbstractBlockParser {

    private final BlockQuote block = new BlockQuote();

    public BlockQuoteParser(SourcePosition pos) {
        block.setSourcePosition(pos);
    }

    @Override
    public boolean canContain(Block block) {
        return true;
    }

    @Override
    public boolean shouldTryBlockStarts() {
        return true;
    }

    @Override
    public BlockContinue tryContinue(ParserState state) {
        int nextNonSpace = state.getNextNonSpaceIndex();
        CharSequence line = state.getLine();
        int indent = nextNonSpace - state.getIndex();
        if (indent <= 3 && nextNonSpace < line.length() && line.charAt(nextNonSpace) == '>') {
            int newIndex = nextNonSpace + 1;
            if (newIndex < line.length() && line.charAt(newIndex) == ' ') {
                newIndex++;
            }
            return BlockContinue.of(newIndex);
        } else {
            return BlockContinue.none();
        }
    }

    @Override
    public BlockQuote getBlock() {
        return block;
    }

    public static class Factory extends AbstractBlockParserFactory {
        public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
            CharSequence line = state.getLine();
            int nextNonSpace = state.getNextNonSpaceIndex();
            if (line.charAt(nextNonSpace) == '>') {
                int newOffset = nextNonSpace + 1;
                // optional following space
                if (newOffset < line.length() && line.charAt(newOffset) == ' ') {
                    newOffset++;
                }
                return BlockStart.of(new BlockQuoteParser(pos(state, nextNonSpace)), newOffset);
            } else {
                return BlockStart.none();
            }
        }
    }
}
